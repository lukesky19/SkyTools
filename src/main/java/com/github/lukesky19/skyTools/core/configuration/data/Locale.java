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
package com.github.lukesky19.skyTools.core.configuration.data;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This record contains the plugin's locale configuration.
 * @param configVersion The config version.
 * @param prefix The plugin's prefix.
 * @param help The plugin's help message.
 * @param reload The plugin's reload message.
 * @param buildTool The plugin's {@link BuildToolMessages}.
 * @param mobCaptureTool The plugin's {@link MobCaptureToolMessages}.
 */
@ConfigSerializable
public record Locale(
        String configVersion,
        String prefix,
        List<String> help,
        String reload,
        @NotNull BuildToolMessages buildTool,
        @NotNull MobCaptureToolMessages mobCaptureTool) {
    /**
     * The locale messages for the build tool.
     * @param toolGiven The message sent to the command executor who give a player a build tool.
     * @param playerToolGiven The message sent to the player who receives a build tool.
     * @param configError The message sent to the player when a build tool cannot be created or updated due to a config error.
     * @param positionOneInvalid The message sent to the player when position 1 is invalid.
     * @param positionTwoInvalid The message sent to the player when position 2 is invalid.
     * @param setPositionOne The message sent to the player when position 1 is set.
     * @param setPositionTwo The message sent to the player when position 2 is set.
     * @param buildQueued The message sent to the player when a build is queued.
     * @param noAccess The message sent when the player doesn't have access to use the build tool.
     */
    @ConfigSerializable
    public record BuildToolMessages(
            String toolGiven,
            String playerToolGiven,
            String configError,
            String positionOneInvalid,
            String positionTwoInvalid,
            String setPositionOne,
            String setPositionTwo,
            String noAccess,
            String buildQueued) {}
    /**
     * The locale messages for the mob capture tool.
     * @param toolGiven The message sent to the command executor who give a player a mob capture tool.
     * @param playerToolGiven The message sent to the player who receives a mob capture tool.
     * @param toolUsedUp The message sent tot he player when their mob capture tool runs out of uses.
     * @param configError The message sent to the player when a mob capture tool cannot be created or updated due to a config error.
     * @param noAccess The message sent when the player doesn't have access to use the mob capture tool.
     */
    @ConfigSerializable
    public record MobCaptureToolMessages(
            String toolGiven,
            String playerToolGiven,
            String toolUsedUp,
            String configError,
            String noAccess) {}
}