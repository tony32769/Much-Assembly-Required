package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.CpuHardware;
import net.simon987.server.assembly.Status;
import org.json.simple.JSONObject;

public class CubotHologram extends CpuHardware {


    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x0009;

    public static final int DEFAULT_ADDRESS = 9;

    private Cubot cubot;

    private static final int CLEAR = 0;
    private static final int DISPLAY_HEX = 1;
    private static final int DISPLAY_STRING = 2;

    private static final int STR_MAX_LEN = 8;

    public CubotHologram(Cubot cubot) {
        this.cubot = cubot;
    }

    @Override
    public void handleInterrupt(Status status) {

        char a = getCpu().getRegisterSet().getRegister("A").getValue();

        if (a == CLEAR) {
            cubot.setHologramMode(Cubot.HologramMode.CLEARED);
        } else if (a == DISPLAY_HEX) {
            char b = getCpu().getRegisterSet().getRegister("B").getValue();
            cubot.setHologram(b);
            cubot.setHologramMode(Cubot.HologramMode.HEX);
        } else if (a == DISPLAY_STRING) {
            char x = getCpu().getRegisterSet().getRegister("X").getValue();
            //Display zero-terminated string starting at X (max 8 chars)

            StringBuilder holoString = new StringBuilder();

            for (int i = 0; i < STR_MAX_LEN; i++) {

                char nextChar = (char) getCpu().getMemory().get(x + i);

                if (nextChar != 0) {
                    holoString.append((char) getCpu().getMemory().get(x + i));
                } else {
                    break;
                }
            }

            cubot.setHologramString(holoString.toString());
            cubot.setHologramMode(Cubot.HologramMode.STRING);
        }

    }

    @Override
    public char getId() {
        return HWID;
    }

    public static CubotHologram deserialize(JSONObject hwJSON) {
        return new CubotHologram((Cubot) GameServer.INSTANCE.getGameUniverse().getObject((int) (long) hwJSON.get("cubot")));
    }

    @Override
    public JSONObject serialise() {
        JSONObject json = new JSONObject();
        json.put("hwid", (int) HWID);
        json.put("cubot", cubot.getObjectId());

        return json;
    }
}
