package sqlancer.state;

import sqlancer.*;
import sqlancer.SQLGlobalState;
import sqlancer.common.schema.AbstractSchema;
import sqlancer.common.schema.Schema;

public interface GlobalState<O extends DBMSOptions, S extends Schema<?>, C extends SQLancerDBConnection> {

    void setConnection(C con);

    C getConnection();

    void setDBMSOptions(O dbmsOptions);

    O getDBMSOptions();

    void setRandomly(Randomly r);

    Randomly getRandomly();

    MainOptions getOptions();

    void setMainOptions(MainOptions options);

    void setStateLogger(Main.StateLogger logger);

    Main.StateLogger getLogger();

    void setState(StateToReproduce state);

    StateToReproduce getState();

    String getDatabaseName();

    void setDatabaseName(String databaseName);

    S getSchema();
    void updateSchema() throws Exception;

//    protected void setSchema(S schema);
//    protected abstract S readSchema() throws Exception;
}
