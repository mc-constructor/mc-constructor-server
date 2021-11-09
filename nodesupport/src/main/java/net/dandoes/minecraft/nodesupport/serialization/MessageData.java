package net.dandoes.minecraft.nodesupport.serialization;

import com.google.gson.Gson;

import java.util.UUID;

public class MessageData<TDataSource> {
    /**
     * Utility class for defining and serializing messages sent to an external socket client.
     */

    protected static final String linePartDelimiter = "|";
    private static final Gson gson = new Gson();
    private static final String newline = "\n";

    public final UUID id;

    private final transient TDataSource dataSource;

    protected MessageData(final TDataSource dataSource) {
        this.id = UUID.randomUUID();
        this.dataSource = dataSource;
    }

    protected TDataSource getDataSource() {
        return this.dataSource;
    }

    public String toJson() {
        return gson.toJson(this);
    }

    /**
     *
     * @param delimiter The delimiter appended to the end of each transmission, which signals the
     *                  end of the transmission
     * @return The fully serialized transmission content for the message that will be transmitted
     *         over the socket connection
     *
     * The transmission is broken into multiple lines, separated by newline characters (\n):
     *
     * Line 1    - Header: RequestID, plus any additional metadata separated by | characters
     * Line 2... - Content: Serialized message content
     * Last line - Transmission delimiter (passed via "delimiter" argument)
     *
     * Examples:
     *
     *   Event broadcast - transmission ID in header, JSON serialized content:
     *      6459e9e9-b8bd-4182-ba76-00b652bc89fb
     *      {"id":"6459e9e9-b8bd-4182-ba76-00b652bc89fb","name":"net.minecraftforge.event.entity.player.PlayerEvent", "player":...}
     *      ----------------------------------
     *
     *   Command response - transmission ID, originating request ID, success status in header,
     *                      JSON serialized content:
     *      83ca11c0-8cd9-458d-849a-18c33d09542e|1bedb823-748b-49af-abc4-fb0f5beb1e77|true
     *      {"id":"83ca11c0-8cd9-458d-849a-18c33d09542e","requestId":"1bedb823-748b-49af-abc4-fb0f5beb1e77","message":"Well done","success":true}
     *      ----------------------------------
     */
    public String toTransmissionContent(final String delimiter) {
        return String.join(newline, new String[] {
                this.getTransmissionHeader(),
                this.toJson(),
                delimiter
        });
    }

    protected String getTransmissionHeader() {
        return String.join(linePartDelimiter, this.getTransmissionHeaderParts());
    }

    protected String[] getTransmissionHeaderParts() {
        return new String[]{this.id.toString()};
    }
}
