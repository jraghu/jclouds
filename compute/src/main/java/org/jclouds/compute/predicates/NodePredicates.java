/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.util.Utils.checkNotEmpty;

import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

/**
 * Container for node filters (predicates).
 * 
 * This class has static methods that create customized predicates to use with
 * {@link org.jclouds.compute.ComputeService}.
 * 
 * @author Oleksiy Yarmula
 */
public class NodePredicates {

   /**
    * Return nodes with the specific ids Note: returns all nodes, regardless of the state.
    * 
    * @param ids
    *           ids of the resources
    * @return predicate
    */
   public static Predicate<ComputeMetadata> withIds(String... ids) {
      checkNotNull(ids, "ids must be defined");
      final Set<String> search = Sets.newHashSet(ids);
      return new Predicate<ComputeMetadata>() {
         @Override
         public boolean apply(@Nullable ComputeMetadata nodeMetadata) {
            return search.contains(nodeMetadata.getProviderId());
         }

         @Override
         public String toString() {
            return "withIds(" + search + ")";
         }
      };
   }

   /**
    * return everything.
    */
   public static Predicate<ComputeMetadata> all() {
      return Predicates.<ComputeMetadata> alwaysTrue();
   }

   /**
    * Return nodes with specified tag. Note: returns all nodes, regardless of the state.
    * 
    * @param tag
    *           tag to match the items
    * @return predicate
    */
   public static Predicate<NodeMetadata> withTag(final String tag) {
      checkNotEmpty(tag, "Tag must be defined");
      return new Predicate<NodeMetadata>() {
         @Override
         public boolean apply(@Nullable NodeMetadata nodeMetadata) {
            return tag.equals(nodeMetadata.getTag());
         }

         @Override
         public String toString() {
            return "withTag(" + tag + ")";
         }
      };
   }

   /**
    * Return nodes with specified tag that are in the RUNNING state.
    * 
    * @param tag
    *           tag to match the items
    * @return predicate
    */
   public static Predicate<NodeMetadata> runningWithTag(final String tag) {
      checkNotEmpty(tag, "Tag must be defined");
      return new Predicate<NodeMetadata>() {
         @Override
         public boolean apply(NodeMetadata nodeMetadata) {
            return tag.equals(nodeMetadata.getTag())
                     && nodeMetadata.getState() == NodeState.RUNNING;
         }

         @Override
         public String toString() {
            return "runningWithTag(" + tag + ")";
         }
      };
   }

   /**
    * Match nodes with State == RUNNING
    */
   public static final Predicate<NodeMetadata> ACTIVE = new Predicate<NodeMetadata>() {
      @Override
      public boolean apply(NodeMetadata nodeMetadata) {
         return nodeMetadata.getState() == NodeState.RUNNING;
      }

      @Override
      public String toString() {
         return "ACTIVE";
      }
   };

   /**
    * Match nodes with State == TERMINATED
    */
   public static final Predicate<NodeMetadata> TERMINATED = new Predicate<NodeMetadata>() {
      @Override
      public boolean apply(NodeMetadata nodeMetadata) {
         return nodeMetadata.getState() == NodeState.TERMINATED;
      }

      @Override
      public String toString() {
         return "TERMINATED";
      }
   };

}
