package github.cweijan.ultimate.db.config;

import github.cweijan.ultimate.db.DatabaseType;
import github.cweijan.ultimate.db.init.generator.TableAutoMode;
import github.cweijan.ultimate.util.Log;
import github.cweijan.ultimate.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ultimate.jdbc")
public class DbConfig {

    public static final String UTF8_ENCODING = "characterEncoding=utf-8";
    private String username;
    private String password;
    private String driver;
    private String url;
    /**
     * Hikari datasource maximumPoolSize.
     */
    private int maximumPoolSize = 20;

    /**
     * Hikari datasource minimumIdle.
     */
    private int minimumIdle = 5;
    /**
     * Enable show jdbc sql.
     */
    private boolean showSql = true;
    /**
     * Enable db-ultimate.
     */
    private boolean enable = true;
    /**
     * Develop mode support hotswap component
     */
    private boolean develop;
    /**
     * Component bean scan package
     */
    private String scanPackage;
    /**
     * Table auto create option, this feature is not stable.
     */
    private TableAutoMode tableMode = TableAutoMode.none;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        if (StringUtils.isNotEmpty(url) && !url.contains(UTF8_ENCODING)) {
            url += (!url.contains("?")) ? ("?" + UTF8_ENCODING) : ("&" + UTF8_ENCODING);
        }
        this.url = url;
    }

    public boolean configCheck() {
        boolean enable = true;
        if (!this.enable) {
            Log.info("db-ultimate is disabled, skip..");
            enable = false;
        } else if (this.url == null) {
            Log.error("jdbc url property not found! skip..");
            enable = false;
        } else if (this.driver == null) {
            Log.error("jdbc driver name property not found! skip..");
            enable = false;
        } else if (this.username == null) {
            Log.error("jdbc username property not found! skip..");
            enable = false;
        } else if (this.password == null) {
            Log.error("jdbc password property not found! skip..");
            enable = false;
        }

        return enable;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getDriver() {
        return this.driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }


    public int getMaximumPoolSize() {
        return this.maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getMinimumIdle() {
        return this.minimumIdle;
    }

    public void setMinimumIdle(int minimumIdle) {
        this.minimumIdle = minimumIdle;
    }

    public boolean getShowSql() {
        return this.showSql;
    }

    public void setShowSql(boolean showSql) {
        this.showSql = showSql;
    }

    public boolean getEnable() {
        return this.enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean getDevelop() {
        return this.develop;
    }

    public void setDevelop(boolean develop) {
        this.develop = develop;
    }


    public TableAutoMode getTableMode() {
        return this.tableMode;
    }

    public void setTableMode(TableAutoMode tableMode) {
        this.tableMode = tableMode;
    }


    public String getScanPackage() {
        return this.scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    @NotNull
    public DatabaseType getDatabaseType() {
        return DatabaseType.getDatabaseType(url);
    }
}
