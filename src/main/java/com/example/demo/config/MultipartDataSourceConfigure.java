package com.example.demo.config;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/21.
 */
@Configuration
@ConfigurationProperties(prefix = "spring")
public class MultipartDataSourceConfigure {
    private Map<String, Object> datasource = new HashMap<String, Object>();
    private List<Map<String, Object>> datasources = new ArrayList<Map<String, Object>>();


    @Bean
    public DataSource druidDataSource() {
        MultipartDataSource m = new MultipartDataSource();
        return m;
    }


    class MultipartDataSource extends AbstractRoutingDataSource implements EnvironmentAware {
        private final String DATASOURCE_TYPE_DEFAULT = "org.apache.tomcat.jdbc.pool.DataSource";
        private final Map<String, Map<String, Object>> dataSourceParams = new HashMap<String, Map<String, Object>>();
        private final Map<Object, Object> dataSources = new MapMaker().makeMap();
        private final Map<Object, Object> cacheSources = new MapMaker().makeComputingMap(new Function<Object, Object>() {

            public Object apply(Object s) {
                DataSource d = (DataSource) dataSources.get(s);
                if (d != null) {
                    bindDataSource(d, dataSourceParams.get(s.toString()));
                    System.out.println("dataSource create:[" + s + "]");
                    return d;
                }
                return null;
            }
        });

        @Override
        protected Object determineCurrentLookupKey() {
            if (DBHolder.getDb() != null) {
                Object o = cacheSources.get(DBHolder.getDb());
                if (o != null) {
                    return DBHolder.getDb();
                }
            }
            return null;
        }

        public void setEnvironment(Environment env) {
            DataSource defaultDataSource = createDataSource(datasource);
            bindDataSource(defaultDataSource, datasource);

            Map<String, Object> baseParam = datasource;
            if (datasources != null) {
                for (Map<String, Object> m : datasources) {
                    Object name = m.get("name");
                    if (name != null) {
                        Map<String, Object> par = new HashMap<String, Object>();
                        par.putAll(baseParam);
                        par.putAll(m);
                        dataSourceParams.put(name.toString(), par);
                        dataSources.put(name, createDataSource(par));
                    }
                }
            }

            this.setDefaultTargetDataSource(defaultDataSource);
            this.setTargetDataSources(dataSources);
        }

        private void bindDataSource(DataSource dataSource, Map<String, Object> params) {
            RelaxedDataBinder dataBinder = new RelaxedDataBinder(dataSource);
            dataBinder.bind(new MutablePropertyValues(params));
        }

        private DataSource createDataSource(Map<String, Object> params) {
            Object type = params.get("type");
            if (type == null) {
                type = DATASOURCE_TYPE_DEFAULT;
            }
            DataSource dataSource = null;
            try {
                dataSource = (DataSource) Class.forName((String) type).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dataSource;
        }
    }

    public Map<String, Object> getDatasource() {
        return datasource;
    }

    public void setDatasource(Map<String, Object> datasource) {
        this.datasource = datasource;
    }

    public List<Map<String, Object>> getDatasources() {
        return datasources;
    }

    public void setDatasources(List<Map<String, Object>> datasources) {
        this.datasources = datasources;
    }

}

