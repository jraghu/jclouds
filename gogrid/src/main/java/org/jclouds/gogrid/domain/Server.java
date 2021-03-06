/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.gogrid.domain;

import com.google.common.primitives.Longs;

/**
 * @author Oleksiy Yarmula
 */
public class Server implements Comparable<Server> {

    private long id;
    private boolean isSandbox;
    private String name;
    private String description;
    private Option state;

    private Option type;
    private Option ram;
    private Option os;
    private Ip ip;

    private ServerImage image;

    /**
     * A no-args constructor is required for deserialization
     */
    public Server() {
    }

    public Server(long id, boolean sandbox, String name,
                  String description, Option state, Option type,
                  Option ram, Option os, Ip ip, ServerImage image) {
        this.id = id;
        this.isSandbox = sandbox;
        this.name = name;
        this.description = description;
        this.state = state;
        this.type = type;
        this.ram = ram;
        this.os = os;
        this.ip = ip;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public boolean isSandbox() {
        return isSandbox;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Option getState() {
        return state;
    }

    public Option getType() {
        return type;
    }

    public Option getRam() {
        return ram;
    }

    public Option getOs() {
        return os;
    }

    public Ip getIp() {
        return ip;
    }

    public ServerImage getImage() {
        return image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Server server = (Server) o;

        if (id != server.id) return false;
        if (isSandbox != server.isSandbox) return false;
        if (description != null ? !description.equals(server.description) : server.description != null) return false;
        if (image != null ? !image.equals(server.image) : server.image != null) return false;
        if (ip != null ? !ip.equals(server.ip) : server.ip != null) return false;
        if (!name.equals(server.name)) return false;
        if (os != null ? !os.equals(server.os) : server.os != null) return false;
        if (ram != null ? !ram.equals(server.ram) : server.ram != null) return false;
        if (!state.equals(server.state)) return false;
        if (!type.equals(server.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (isSandbox ? 1 : 0);
        result = 31 * result + name.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + state.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (ram != null ? ram.hashCode() : 0);
        result = 31 * result + (os != null ? os.hashCode() : 0);
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Server o) {
        return Longs.compare(id, o.getId());
    }

    @Override
    public String toString() {
        return "Server{" +
                "id=" + id +
                ", isSandbox=" + isSandbox +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", state=" + state +
                ", type=" + type +
                ", ram=" + ram +
                ", os=" + os +
                ", ip=" + ip +
                ", image=" + image +
                '}';
    }
}
