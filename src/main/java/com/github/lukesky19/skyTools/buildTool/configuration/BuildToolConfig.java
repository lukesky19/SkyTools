/*
    SkyTools adds new unique tools to the game.
    Copyright (C) 2026 lukeskywlker19

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.github.lukesky19.skyTools.buildTool.configuration;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import com.github.lukesky19.skylib.paper.api.itemstack.ItemStackConfig;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * This record holds the build tool configuration.
 * @param version The config version.
 * @param restrictedWorlds The world names the build tool is not allowed in.
 * @param restrictedRegions The region names the build tool is not allowed in.
 * @param item The {@link ItemStackConfig} for the build tool.
 */
@ConfigSerializable
public record BuildToolConfig(
        int version,
        @NonNull List<String> restrictedWorlds,
        @NonNull List<String> restrictedRegions,
        @NonNull ItemStackConfig item) {}