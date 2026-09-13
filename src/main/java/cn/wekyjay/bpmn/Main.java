package cn.wekyjay.bpmn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * SDK 使用示例：读取 JSON 流程定义文件，输出 BPMN 2.0 XML。
 * 用法：java cn.wekyjay.bpmn.Main [json文件路径] [输出xml路径]
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Path jsonPath = Path.of(args.length > 0 ? args[0] : "examples/process.json");
        Path outputPath = Path.of(args.length > 1 ? args[1] : "target/test.bpmn20.xml");

        String json = Files.readString(jsonPath);
        byte[] xmlBytes = new BpmnConverter().convertToXml(json);

        Files.createDirectories(outputPath.toAbsolutePath().getParent());
        Files.write(outputPath, xmlBytes);
        System.out.println("BPMN XML 已生成: " + outputPath.toAbsolutePath());
    }
}
