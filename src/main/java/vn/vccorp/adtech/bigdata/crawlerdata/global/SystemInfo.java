package vn.vccorp.adtech.bigdata.crawlerdata.global;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

/**
 * Created by thuyenhx on 26/08/2015.
 */
public class SystemInfo {
    private static Configuration configuration;

    private static String configFilePath = "conf.properties";

    static {
        try {
            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(configFilePath);
            FileChangedReloadingStrategy reloadingStrategy = new FileChangedReloadingStrategy();
            propertiesConfiguration.setReloadingStrategy(reloadingStrategy);
            configuration = propertiesConfiguration;

        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    private SystemInfo() {

    }

    public static synchronized Configuration getConfiguration() {
        if (configuration == null)
            try {
                configuration = new PropertiesConfiguration(configFilePath);
            } catch (ConfigurationException e) {
                e.printStackTrace();
                System.exit(15);
            }
        return configuration;
    }
}
