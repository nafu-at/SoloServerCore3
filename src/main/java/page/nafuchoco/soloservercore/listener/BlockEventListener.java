/*
 * Copyright 2020 NAFU_at
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package page.nafuchoco.soloservercore.listener;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import page.nafuchoco.soloservercore.CoreProtectClient;
import page.nafuchoco.soloservercore.SoloServerApi;
import page.nafuchoco.soloservercore.database.PluginSettingsManager;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Cancel changes to the block if a record of changes is found that meets the condition.<br>
 * Bypass if user has the permission <code>soloservercore.protect.bypass</code>.
 */
public class BlockEventListener implements Listener {
    private final CoreProtectClient coreProtect;
    private final PluginSettingsManager settingsManager;

    public BlockEventListener(CoreProtectClient coreProtect, PluginSettingsManager settingsManager) {
        this.coreProtect = coreProtect;
        this.settingsManager = settingsManager;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockDamageEvent(BlockDamageEvent event) {
        if (!checkEditRights(event.getBlock(), event.getPlayer())) {
            event.getPlayer().sendMessage(ChatColor.GRAY + "どうやら誰かの手によって作られた人工物のようだ...");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (!checkEditRights(event.getBlock(), event.getPlayer())) {
            event.getPlayer().sendMessage(ChatColor.GRAY + "どうやら誰かの手によって作られた人工物のようだ...");
            event.setCancelled(true);
        }
    }

    private boolean checkEditRights(Block block, Player player) {
        // Permission Check
        if (!player.hasPermission("soloservercore.protect.bypass")) {
            String actionPlayer = coreProtect.getAction(block, settingsManager.getProtectionPeriod());
            // Action Player Check
            if (actionPlayer != null && !actionPlayer.startsWith("#") && !player.getName().equals(actionPlayer)) {
                val sscPlayer = SoloServerApi.getInstance().getSSCPlayer(player);
                if (sscPlayer.getJoinedTeam() != null) {
                    val joinedTeam = sscPlayer.getJoinedTeam();
                    val members = new ArrayList<UUID>();
                    members.addAll(joinedTeam.getMembers());
                    members.add(joinedTeam.getOwner());
                    // Action Team Member Check
                    for (val uuid : members) {
                        val member = Bukkit.getOfflinePlayer(uuid).getName();
                        if (member.equals(actionPlayer))
                            return true;
                    }
                }
            } else {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }
}
