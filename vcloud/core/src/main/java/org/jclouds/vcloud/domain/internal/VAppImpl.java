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
package org.jclouds.vcloud.domain.internal;

import java.net.URI;
import java.util.Set;

import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.ResourceType;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.domain.VirtualSystem;

import com.google.common.base.Function;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class VAppImpl implements VApp {
   private final String id;
   private final String name;
   private final URI location;
   private final NamedResource vDC;
   private final VAppStatus status;
   private final Long size;
   private final ListMultimap<String, String> networkToAddresses;
   private final String operatingSystemDescription;
   private final VirtualSystem system;
   private final Set<ResourceAllocation> resourceAllocations;
   private final ListMultimap<ResourceType, ResourceAllocation> resourceAllocationByType;

   /** The serialVersionUID */
   private static final long serialVersionUID = 8464716396538298809L;

   public VAppImpl(String id, String name, URI location, VAppStatus status, Long size,
            NamedResource vDC, ListMultimap<String, String> networkToAddresses,
            String operatingSystemDescription, VirtualSystem system,
            Set<ResourceAllocation> resourceAllocations) {
      this.id = id;
      this.name = name;
      this.location = location;
      this.status = status;
      this.size = size;
      this.vDC = vDC;
      this.networkToAddresses = networkToAddresses;
      this.operatingSystemDescription = operatingSystemDescription;
      this.system = system;
      this.resourceAllocations = resourceAllocations;
      resourceAllocationByType = Multimaps.index(resourceAllocations,
               new Function<ResourceAllocation, ResourceType>() {
                  @Override
                  public ResourceType apply(ResourceAllocation from) {
                     return from.getType();
                  }
               });
   }

   public VAppStatus getStatus() {
      return status;
   }

   public ListMultimap<String, String> getNetworkToAddresses() {
      return networkToAddresses;
   }

   public String getOperatingSystemDescription() {
      return operatingSystemDescription;
   }

   public VirtualSystem getSystem() {
      return system;
   }

   public Set<ResourceAllocation> getResourceAllocations() {
      return resourceAllocations;
   }

   public Multimap<ResourceType, ResourceAllocation> getResourceAllocationByType() {
      return resourceAllocationByType;
   }

   public NamedResource getVDC() {
      return vDC;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((location == null) ? 0 : location.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((networkToAddresses == null) ? 0 : networkToAddresses.hashCode());
      result = prime * result
               + ((operatingSystemDescription == null) ? 0 : operatingSystemDescription.hashCode());
      result = prime * result
               + ((resourceAllocationByType == null) ? 0 : resourceAllocationByType.hashCode());
      result = prime * result
               + ((resourceAllocations == null) ? 0 : resourceAllocations.hashCode());
      result = prime * result + ((size == null) ? 0 : size.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + ((system == null) ? 0 : system.hashCode());
      result = prime * result + ((vDC == null) ? 0 : vDC.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      VAppImpl other = (VAppImpl) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (location == null) {
         if (other.location != null)
            return false;
      } else if (!location.equals(other.location))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (networkToAddresses == null) {
         if (other.networkToAddresses != null)
            return false;
      } else if (!networkToAddresses.equals(other.networkToAddresses))
         return false;
      if (operatingSystemDescription == null) {
         if (other.operatingSystemDescription != null)
            return false;
      } else if (!operatingSystemDescription.equals(other.operatingSystemDescription))
         return false;
      if (resourceAllocationByType == null) {
         if (other.resourceAllocationByType != null)
            return false;
      } else if (!resourceAllocationByType.equals(other.resourceAllocationByType))
         return false;
      if (resourceAllocations == null) {
         if (other.resourceAllocations != null)
            return false;
      } else if (!resourceAllocations.equals(other.resourceAllocations))
         return false;
      if (size == null) {
         if (other.size != null)
            return false;
      } else if (!size.equals(other.size))
         return false;
      if (status == null) {
         if (other.status != null)
            return false;
      } else if (!status.equals(other.status))
         return false;
      if (system == null) {
         if (other.system != null)
            return false;
      } else if (!system.equals(other.system))
         return false;
      if (vDC == null) {
         if (other.vDC != null)
            return false;
      } else if (!vDC.equals(other.vDC))
         return false;
      return true;
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public URI getLocation() {
      return location;
   }

   public Long getSize() {
      return size;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", location=" + location + ", name=" + name + ", networkToAddresses="
               + networkToAddresses + ", operatingSystemDescription=" + operatingSystemDescription
               + ", resourceAllocationByType=" + resourceAllocationByType
               + ", resourceAllocations=" + resourceAllocations + ", size=" + size + ", status="
               + status + ", system=" + system + ", vDC=" + vDC + "]";
   }

   @Override
   public String getType() {
      return VCloudMediaType.VAPP_XML;
   }

   @Override
   public int compareTo(NamedResource o) {
      return (this == o) ? 0 : getId().compareTo(o.getId());
   }
}