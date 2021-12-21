package org.grain.mybatis.generator;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;

/**
 * @author wulifu
 */
public class Generator {
    private static String url = "jdbc:mysql://39.105.117.177:3306/test?serverTimezone=UTC";

    public static void main(String[] args) {
        generate();
    }

    public static void generate() {
        //1. 全局配置
        GlobalConfig config = new GlobalConfig.Builder()
                .author("grain")
                .outputDir(System.getProperty("user.dir") + "/outPut")
                .fileOverride()
                .enableSwagger()
                .build();
        //2. 数据源配置
        DataSourceConfig dsConfig = new DataSourceConfig.Builder(url, "root", "1qaz@WSX")
                .dbQuery(new MySqlQuery())
                .keyWordsHandler(new MySqlKeyWordsHandler())
                .build();
        //3. 策略配置globalConfiguration中
        StrategyConfig stConfig = new StrategyConfig.Builder().enableCapitalMode().build();

        //4. 包名策略配置
        PackageConfig pkConfig = new PackageConfig.Builder()
                .parent("org.grain")
                .mapper("dao")
                .service("service")
                .serviceImpl("serviceImpl")
                .controller("controller")
                .build();
        //5. 整合配置
        AutoGenerator autoGenerator = new AutoGenerator(dsConfig).global(config).strategy(stConfig).packageInfo(pkConfig);
        //6. 执行
        autoGenerator.execute();
    }
}
