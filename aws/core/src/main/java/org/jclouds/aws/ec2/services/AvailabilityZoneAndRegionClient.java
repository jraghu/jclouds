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
package org.jclouds.aws.ec2.services;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.jclouds.aws.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.aws.ec2.options.DescribeAvailabilityZonesOptions;
import org.jclouds.aws.ec2.options.DescribeRegionsOptions;
import org.jclouds.concurrent.Timeout;

/**
 * Provides EC2 Availability Zones and Regions services for EC2.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 45, timeUnit = TimeUnit.SECONDS)
public interface AvailabilityZoneAndRegionClient {

   /**
    * Displays Availability Zones that are currently available to the account and their states.
    * 
    * @see InstanceClient#runInstances
    * @see #describeRegions
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeAvailabilityZones.html"
    *      />
    */
   Set<AvailabilityZoneInfo> describeAvailabilityZonesInRegion(@Nullable String region,
            DescribeAvailabilityZonesOptions... options);

   /**
    * Describes Regions that are currently available to the account.
    * 
    * @see InstanceClient#runInstances
    * @see #describeAvailabilityZones
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeRegions.html"
    *      />
    */
   Map<String, URI> describeRegions(DescribeRegionsOptions... options);
}
