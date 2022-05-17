package cn.uniondrug.dev.util;

import cn.uniondrug.dev.ApiDetail;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import java.io.StringWriter;
import java.util.Properties;

/**
 * 根据模版生成对应的内容
 *
 * @author liuzhihang
 * @date 2020/11/21 15:38
 */
public class VelocityUtil {

    private static VelocityEngine engine;
    private static String VM_LOG_TAG = "UniondrugDevKit VelocityUtils";

    static {
        engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.PARSER_POOL_SIZE, 20);
        engine.setProperty(RuntimeConstants.INPUT_ENCODING, "UTF-8");
        engine.setProperty(RuntimeConstants.OUTPUT_ENCODING, "UTF-8");

        Properties props = new Properties();
        props.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        props.put("runtime.log.logsystem.log4j.category", "velocity");
        props.put("runtime.log.logsystem.log4j.logger", "velocity");
        engine.init(props);
    }

    public static String convert(String template, ApiDetail data) {
        StringWriter writer = new StringWriter();
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("item", data.getApi());
        velocityContext.put("detail", data);
        boolean isSuccess = engine.evaluate(velocityContext, writer, VM_LOG_TAG, template);
        if (!isSuccess) {
            return "ERROR";
        }
        return writer.toString();
    }

}
