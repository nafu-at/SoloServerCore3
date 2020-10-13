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

package page.nafuchoco.soloservercore;

import org.bukkit.configuration.file.FileConfiguration;

public class SoloServerCoreConfig {
    private static final SoloServerCore instance = SoloServerCore.getInstance();
    private InitConfig initConfig;
    private boolean debug;

    public void reloadConfig() {
        instance.reloadConfig();
        FileConfiguration config = instance.getConfig();

        if (initConfig == null) {
            DatabaseType databaseType = DatabaseType.valueOf(config.getString("initialization.database.type"));
            String address = config.getString("initialization.database.address");
            int port = config.getInt("initialization.database.port", 3306);
            String database = config.getString("initialization.database.database");
            String username = config.getString("initialization.database.username");
            String password = config.getString("initialization.database.password");
            String tablePrefix = config.getString("initialization.database.tablePrefix");
            String spawnWorlds = config.getString("initialization.spawn.spawnWorld");
            int generateLocationRange = config.getInt("initialization.spawn.generateLocationRange");
            initConfig = new InitConfig(databaseType, address, port, database, username, password, tablePrefix, spawnWorlds, generateLocationRange);
            debug = config.getBoolean("debug");
        }
    }

    public InitConfig getInitConfig() {
        return initConfig;
    }

    public boolean isDebug() {
        return debug;
    }

    public enum DatabaseType {
        MARIADB("org.mariadb.jdbc.Driver", "jdbc:mariadb://"),
        MYSQL("com.mysql.jdbc.Driver", "jdbc:mysql://");

        private final String jdbcClass;
        private final String addressPrefix;

        DatabaseType(String jdbcClass, String addressPrefix) {
            this.jdbcClass = jdbcClass;
            this.addressPrefix = addressPrefix;
        }

        public String getJdbcClass() {
            return jdbcClass;
        }

        public String getAddressPrefix() {
            return addressPrefix;
        }
    }

    public static class InitConfig {
        private DatabaseType databaseType;
        private String address;
        private int port;
        private String database;
        private String username;
        private String password;
        private String tablePrefix;

        private String spawnWorld;
        private int generateLocationRange;

        public InitConfig(DatabaseType databaseType, String address, int port, String database, String username, String password, String tablePrefix, String spawnWorld, int generateLocationRange) {
            this.databaseType = databaseType;
            this.address = address;
            this.port = port;
            this.database = database;
            this.username = username;
            this.password = password;
            this.tablePrefix = tablePrefix;
            this.spawnWorld = spawnWorld;
            this.generateLocationRange = generateLocationRange;
        }

        public DatabaseType getDatabaseType() {
            return databaseType;
        }

        public String getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }

        public String getDatabase() {
            return database;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getTablePrefix() {
            return tablePrefix;
        }

        public String getSpawnWorld() {
            return spawnWorld;
        }

        public int getGenerateLocationRange() {
            return generateLocationRange;
        }
    }
}
