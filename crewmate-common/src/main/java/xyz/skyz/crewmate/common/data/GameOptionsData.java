package xyz.skyz.crewmate.common.data;

import io.netty.buffer.ByteBuf;
import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.common.message.MessageWriter;

import java.util.List;

public class GameOptionsData {

    private short version;
    private short maxPlayers;
    private List<GameKeywords> keywords;
    private byte mapIdRaw;
    private List<GameMaps> mapId;
    private float playerSpeedModifier;
    private float crewLightModifier;
    private float impostorLightModifier;
    private float killCooldown;
    private int numberCommonTasks;
    private int numberLongTasks;
    private int numberShortTasks;
    private int numberEmergencyMeetings;
    private int emergencyCooldown;
    private int numberImpostors;
    private boolean ghostsDoTasks;
    private int killDistance;
    private int discussionTime;
    private int votingTime;
    private boolean confirmImpostor;
    private boolean visualTasks;
    private boolean isDefaults;

    public ByteBuf serialize() {
        MessageWriter writer = new MessageWriter();
        // TODO
        return writer.getByteBuf();
    }

    public void deserialize(MessageReader reader) {
        this.version = reader.readByte();
        //System.out.println("VERSION " + version);
        this.maxPlayers = reader.readByte();
        //System.out.println("MAX PLAYERS " + maxPlayers);
        this.keywords = GameKeywords.parseBitmask(reader.readInt32());
        //System.out.println("KEYWORDS ");
        //keywords.forEach(System.out::println);
        this.mapIdRaw = reader.readSByte();
        this.mapId = GameMaps.parseBitmask(this.mapIdRaw);
        //System.out.println("MAP ID ");
        //mapId.forEach(System.out::println);
        this.playerSpeedModifier = reader.readSingle();
        //System.out.println("PLAYER SPEED MODIFIER " + playerSpeedModifier);
        this.crewLightModifier = reader.readSingle();
        //System.out.println("CREW LIGHT MODIFIER " + crewLightModifier);
        this.impostorLightModifier = reader.readSingle();
        //System.out.println("IMPOSTOR LIGHT MODIFIER " + impostorLightModifier);
        this.killCooldown = reader.readSingle();
        //System.out.println("KILL COOLDOWN " + killCooldown);
        this.numberCommonTasks = reader.readByte();
        //System.out.println("NUMBER COMMON TASKS " + numberCommonTasks);
        this.numberLongTasks = reader.readByte();
        //System.out.println("NUMBER LONG TASKS " + numberLongTasks);
        this.numberShortTasks = reader.readByte();
        //System.out.println("NUMBER SHORT TASKS " + numberShortTasks);
        this.numberEmergencyMeetings = reader.readInt32();
        //System.out.println("NUMBER EMERGENCY MEETINGS " + numberEmergencyMeetings);
        this.numberImpostors = reader.readByte();
        //System.out.println("NUMBER IMPOSTORS " + numberImpostors);
        this.killDistance = reader.readByte();
        //System.out.println("KILL DISTANCE " + killDistance);
        this.discussionTime = reader.readInt32();
        //System.out.println("DISCUSSION TIME " + discussionTime);
        this.votingTime = reader.readInt32();
        //System.out.println("VOTING TIME " + votingTime);
        this.isDefaults = reader.readBoolean();
        //System.out.println("IS DEFAULTS " + isDefaults);
        if (this.version > 1) {
            this.emergencyCooldown = reader.readByte();
            //System.out.println("EMERGENCY COOLDOWN " + emergencyCooldown);
        }
        if (this.version > 2) {
            this.confirmImpostor = reader.readBoolean();
            //System.out.println("CONFIRM IMPOSTOR " + confirmImpostor);
            this.visualTasks = reader.readBoolean();
            //System.out.println("VISUAL TASKS " + visualTasks);
        }
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public short getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(short maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public List<GameKeywords> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<GameKeywords> keywords) {
        this.keywords = keywords;
    }

    public byte getMapIdRaw() {
        return mapIdRaw;
    }

    public void setMapIdRaw(byte mapIdRaw) {
        this.mapIdRaw = mapIdRaw;
    }

    public List<GameMaps> getMapId() {
        return mapId;
    }

    public void setMapId(List<GameMaps> mapId) {
        this.mapId = mapId;
    }

    public float getPlayerSpeedModifier() {
        return playerSpeedModifier;
    }

    public void setPlayerSpeedModifier(float playerSpeedModifier) {
        this.playerSpeedModifier = playerSpeedModifier;
    }

    public float getCrewLightModifier() {
        return crewLightModifier;
    }

    public void setCrewLightModifier(float crewLightModifier) {
        this.crewLightModifier = crewLightModifier;
    }

    public float getImpostorLightModifier() {
        return impostorLightModifier;
    }

    public void setImpostorLightModifier(float impostorLightModifier) {
        this.impostorLightModifier = impostorLightModifier;
    }

    public float getKillCooldown() {
        return killCooldown;
    }

    public void setKillCooldown(float killCooldown) {
        this.killCooldown = killCooldown;
    }

    public int getNumberCommonTasks() {
        return numberCommonTasks;
    }

    public void setNumberCommonTasks(int numberCommonTasks) {
        this.numberCommonTasks = numberCommonTasks;
    }

    public int getNumberLongTasks() {
        return numberLongTasks;
    }

    public void setNumberLongTasks(int numberLongTasks) {
        this.numberLongTasks = numberLongTasks;
    }

    public int getNumberShortTasks() {
        return numberShortTasks;
    }

    public void setNumberShortTasks(int numberShortTasks) {
        this.numberShortTasks = numberShortTasks;
    }

    public int getNumberEmergencyMeetings() {
        return numberEmergencyMeetings;
    }

    public void setNumberEmergencyMeetings(int numberEmergencyMeetings) {
        this.numberEmergencyMeetings = numberEmergencyMeetings;
    }

    public int getEmergencyCooldown() {
        return emergencyCooldown;
    }

    public void setEmergencyCooldown(int emergencyCooldown) {
        this.emergencyCooldown = emergencyCooldown;
    }

    public int getNumberImpostors() {
        return numberImpostors;
    }

    public void setNumberImpostors(int numberImpostors) {
        this.numberImpostors = numberImpostors;
    }

    public int getKillDistance() {
        return killDistance;
    }

    public void setKillDistance(int killDistance) {
        this.killDistance = killDistance;
    }

    public int getDiscussionTime() {
        return discussionTime;
    }

    public void setDiscussionTime(int discussionTime) {
        this.discussionTime = discussionTime;
    }

    public int getVotingTime() {
        return votingTime;
    }

    public void setVotingTime(int votingTime) {
        this.votingTime = votingTime;
    }

    public boolean isConfirmImpostor() {
        return confirmImpostor;
    }

    public void setConfirmImpostor(boolean confirmImpostor) {
        this.confirmImpostor = confirmImpostor;
    }

    public boolean isVisualTasks() {
        return visualTasks;
    }

    public void setVisualTasks(boolean visualTasks) {
        this.visualTasks = visualTasks;
    }

    public boolean isDefaults() {
        return isDefaults;
    }

    public void setDefaults(boolean defaults) {
        isDefaults = defaults;
    }
}
