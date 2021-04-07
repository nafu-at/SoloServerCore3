/*
 * Copyright 2021 NAFU_at
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

package page.nafuchoco.soloservercore.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import page.nafuchoco.soloservercore.event.PlayersTeamDisappearanceEvent;
import page.nafuchoco.soloservercore.event.PlayersTeamJoinEvent;
import page.nafuchoco.soloservercore.event.PlayersTeamLeaveEvent;
import page.nafuchoco.soloservercore.event.PlayersTeamStatusUpdateEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayersTeam {
    private final UUID id;
    private final UUID owner;

    private String teamName;
    private List<UUID> members = new ArrayList<>();

    public PlayersTeam(UUID id, UUID owner) {
        this.id = id;
        this.owner = owner;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getTeamName() {
        return teamName;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setMembers(List<UUID> members) {
        this.members = members;
    }

    public void updateTeamName(Player player, String teamName) {
        String before = getTeamName();
        setTeamName(teamName);
        PlayersTeamStatusUpdateEvent statusUpdateEvent =
                new PlayersTeamStatusUpdateEvent(this, player, PlayersTeamStatusUpdateEvent.UpdatedState.NAME, before, teamName);
        Bukkit.getServer().getPluginManager().callEvent(statusUpdateEvent);
    }

    public void joinTeam(Player player) {
        members.add(player.getUniqueId());
        PlayersTeamJoinEvent joinEvent = new PlayersTeamJoinEvent(this, player);
        Bukkit.getServer().getPluginManager().callEvent(joinEvent);
        if (joinEvent.isCancelled())
            members.remove(player.getUniqueId());
    }

    public void leaveTeam(Player player) {
        if (player.getUniqueId().equals(owner)) {
            PlayersTeamDisappearanceEvent disappearanceEvent = new PlayersTeamDisappearanceEvent(this, player);
            Bukkit.getServer().getPluginManager().callEvent(disappearanceEvent);
        } else {
            members.remove(player.getUniqueId());
            PlayersTeamLeaveEvent leaveEvent = new PlayersTeamLeaveEvent(this, player);
            Bukkit.getServer().getPluginManager().callEvent(leaveEvent);
            if (leaveEvent.isCancelled())
                members.add(player.getUniqueId());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof PlayersTeam)) {
            return false;
        } else {
            PlayersTeam team = (PlayersTeam) obj;
            return id.equals(team.id);
        }
    }
}