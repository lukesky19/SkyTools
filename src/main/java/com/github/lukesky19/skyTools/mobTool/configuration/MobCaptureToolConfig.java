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
package com.github.lukesky19.skyTools.mobTool.configuration;

import com.github.lukesky19.skylib.api.itemstack.ItemStackConfig;
import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This record holds the mob capture tool configuration.
 * @param version The config version.
 * @param restrictedWorlds The world names the mob capture tool is not allowed in.
 * @param restrictedRegions The region names the mob capture tool is not allowed in.
 * @param item The {@link ItemStackConfig} for the mob capture tool.
 */
@ConfigSerializable
public record MobCaptureToolConfig(
        int version,
        @NotNull List<String> restrictedWorlds,
        @NotNull List<String> restrictedRegions,
        @NotNull ItemStackConfig item) {}