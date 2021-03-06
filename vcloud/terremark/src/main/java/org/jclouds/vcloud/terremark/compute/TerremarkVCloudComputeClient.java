/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.compute;

import static org.jclouds.vcloud.terremark.options.AddInternetServiceOptions.Builder.withDescription;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.vcloud.compute.BaseVCloudComputeClient;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.Node;
import org.jclouds.vcloud.terremark.domain.Protocol;
import org.jclouds.vcloud.terremark.domain.PublicIpAddress;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class TerremarkVCloudComputeClient extends BaseVCloudComputeClient {

   private final TerremarkVCloudClient client;
   private final PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider;
   private Provider<String> passwordGenerator;

   @Inject
   protected TerremarkVCloudComputeClient(TerremarkVCloudClient client,
            PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider,
            @Named("PASSWORD") Provider<String> passwordGenerator, Predicate<String> successTester,
            @Named("NOT_FOUND") Predicate<VApp> notFoundTester,
            Map<VAppStatus, NodeState> vAppStatusToNodeState) {
      super(client, successTester, notFoundTester, vAppStatusToNodeState);
      this.client = client;
      this.credentialsProvider = credentialsProvider;
      this.passwordGenerator = passwordGenerator;
   }

   @Override
   protected Map<String, String> parseAndValidateResponse(String templateId, VApp vAppResponse) {
      Credentials credentials = credentialsProvider.execute(client.getVAppTemplate(templateId));
      Map<String, String> toReturn = super.parseResponse(templateId, vAppResponse);
      toReturn.put("username", credentials.account);
      toReturn.put("password", credentials.key);
      return toReturn;
   }

   @Override
   public Map<String, String> start(String vDCId, String name, String templateId,
            InstantiateVAppTemplateOptions options, int... portsToOpen) {
      if (options.getDiskSizeKilobytes() != null) {
         logger.warn("trmk does not support resizing the primary disk; unsetting disk size");
      }
      String password = null;
      if (client.getVAppTemplate(templateId).getDescription().indexOf("Windows") != -1) {
         password = passwordGenerator.get();
         options.getProperties().put("password", password);
      }
      Map<String, String> response = super.start(vDCId, name, templateId, options, portsToOpen);
      if (password != null) {
         response = new LinkedHashMap<String, String>(response);
         response.put("password", password);
      }
      if (portsToOpen.length > 0)
         createPublicAddressMappedToPorts(response.get("id"), portsToOpen);
      return response;
   }

   public String createPublicAddressMappedToPorts(String vAppId, int... ports) {
      VApp vApp = client.getVApp(vAppId);
      PublicIpAddress ip = null;
      String privateAddress = Iterables.getLast(vApp.getNetworkToAddresses().values());
      for (int port : ports) {
         InternetService is = null;
         Protocol protocol;
         switch (port) {
            case 22:
               protocol = Protocol.TCP;
               break;
            case 80:
            case 8080:
               protocol = Protocol.HTTP;
               break;
            case 443:
               protocol = Protocol.HTTPS;
               break;
            default:
               protocol = Protocol.HTTP;
               break;
         }
         if (ip == null) {
            logger.debug(">> creating InternetService in vDC %s:%s:%d", vApp.getVDC().getId(),
                     protocol, port);
            is = client.addInternetServiceToVDC(vApp.getVDC().getId(), vApp.getName() + "-" + port,
                     protocol, port, withDescription(String.format(
                              "port %d access to serverId: %s name: %s", port, vApp.getId(), vApp
                                       .getName())));
            ip = is.getPublicIpAddress();
         } else {
            logger.debug(">> adding InternetService %s:%s:%d", ip.getAddress(), protocol, port);
            is = client.addInternetServiceToExistingIp(ip.getId(), vApp.getName() + "-" + port,
                     protocol, port, withDescription(String.format(
                              "port %d access to serverId: %s name: %s", port, vApp.getId(), vApp
                                       .getName())));
         }
         logger.debug("<< created InternetService(%s) %s:%s:%d", is.getId(), is
                  .getPublicIpAddress().getAddress(), is.getProtocol(), is.getPort());
         logger.debug(">> adding Node %s:%d -> %s:%d", is.getPublicIpAddress().getAddress(), is
                  .getPort(), privateAddress, port);
         Node node = client.addNode(is.getId(), privateAddress, vApp.getName() + "-" + port, port);
         logger.debug("<< added Node(%s)", node.getId());
      }
      return ip != null ? ip.getAddress() : null;
   }

   private Set<PublicIpAddress> deleteInternetServicesAndNodesAssociatedWithVApp(VApp vApp) {
      Set<PublicIpAddress> ipAddresses = Sets.newHashSet();
      SERVICE: for (InternetService service : client.getAllInternetServicesInVDC(vApp.getVDC()
               .getId())) {
         for (Node node : client.getNodes(service.getId())) {
            if (vApp.getNetworkToAddresses().containsValue(node.getIpAddress())) {
               ipAddresses.add(service.getPublicIpAddress());
               logger.debug(">> deleting Node(%s) %s:%d -> %s:%d", node.getId(), service
                        .getPublicIpAddress().getAddress(), service.getPort(), node.getIpAddress(),
                        node.getPort());
               client.deleteNode(node.getId());
               logger.debug("<< deleted Node(%s)", node.getId());
               SortedSet<Node> nodes = client.getNodes(service.getId());
               if (nodes.size() == 0) {
                  logger.debug(">> deleting InternetService(%s) %s:%d", service.getId(), service
                           .getPublicIpAddress().getAddress(), service.getPort());
                  client.deleteInternetService(service.getId());
                  logger.debug("<< deleted InternetService(%s)", service.getId());
                  continue SERVICE;
               }
            }
         }
      }
      return ipAddresses;
   }

   private void deletePublicIpAddressesWithNoServicesAttached(Set<PublicIpAddress> ipAddresses) {
      IPADDRESS: for (PublicIpAddress address : ipAddresses) {
         SortedSet<InternetService> services = client
                  .getInternetServicesOnPublicIp(address.getId());
         if (services.size() == 0) {
            logger.debug(">> deleting PublicIpAddress(%s) %s", address.getId(), address
                     .getAddress());
            client.deletePublicIp(address.getId());
            logger.debug("<< deleted PublicIpAddress(%s)", address.getId());
            continue IPADDRESS;
         }
      }
   }

   /**
    * deletes the internet service and nodes associated with the vapp. Deletes the IP address, if
    * there are no others using it. Finally, it powers off and deletes the vapp. Note that we do not
    * call undeploy, as terremark does not support the command.
    */
   @Override
   public void stop(String id) {
      VApp vApp = client.getVApp(id);
      Set<PublicIpAddress> ipAddresses = deleteInternetServicesAndNodesAssociatedWithVApp(vApp);
      deletePublicIpAddressesWithNoServicesAttached(ipAddresses);
      if (vApp.getStatus() != VAppStatus.OFF) {
         logger.debug(">> powering off vApp(%s), current status: %s", vApp.getId(), vApp
                  .getStatus());
         Task task = client.powerOffVApp(vApp.getId());
         if (!taskTester.apply(task.getId())) {
            throw new TaskException("powerOff", vApp, task);
         }
         vApp = client.getVApp(id);
         logger.debug("<< %s vApp(%s)", vApp.getStatus(), vApp.getId());
      }
      logger.debug(">> deleting vApp(%s)", vApp.getId());
      client.deleteVApp(id);
      boolean successful = notFoundTester.apply(vApp);
      logger.debug("<< deleted vApp(%s) completed(%s)", vApp.getId(), successful);
   }

   @Override
   public Set<String> getPrivateAddresses(String id) {
      VApp vApp = client.getVApp(id);
      return Sets.newHashSet(vApp.getNetworkToAddresses().values());
   }

   @Override
   public Set<String> getPublicAddresses(String id) {
      VApp vApp = client.getVApp(id);
      Set<String> ipAddresses = Sets.newHashSet();
      for (InternetService service : client.getAllInternetServicesInVDC(vApp.getVDC().getId())) {
         for (Node node : client.getNodes(service.getId())) {
            if (vApp.getNetworkToAddresses().containsValue(node.getIpAddress())) {
               ipAddresses.add(service.getPublicIpAddress().getAddress());
            }
         }
      }
      return ipAddresses;
   }
}