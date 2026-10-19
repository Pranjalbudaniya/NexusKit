package com.nexuskit.app.feature.tools.json_converter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class JsonConverterEngineTest {

    @Test
    fun testJsonToCsvBasic() {
        val json = """[{"name":"Alice","age":30},{"name":"Bob","age":25}]"""
        val csvRes = JsonConverterEngine.jsonToCsv(json)
        assertTrue(csvRes.isSuccess)
        val csv = csvRes.getOrThrow()
        assertTrue(csv.contains("name,age") || csv.contains("age,name"))
        assertTrue(csv.contains("Alice"))
        assertTrue(csv.contains("Bob"))
    }

    @Test
    fun testJsonToCsvNestedFlattening() {
        val json = """[{"user":{"name":"Carol","city":"Paris"}}]"""
        val csvRes = JsonConverterEngine.jsonToCsv(json)
        assertTrue(csvRes.isSuccess)
        val csv = csvRes.getOrThrow()
        assertTrue(csv.contains("user.name") && csv.contains("user.city"))
        assertTrue(csv.contains("Carol") && csv.contains("Paris"))
    }

    @Test
    fun testCsvToJsonParsing() {
        val csv = """id,name,role
1,Sarah,Lead
2,John,Developer"""
        val jsonRes = JsonConverterEngine.csvToJson(csv)
        assertTrue(jsonRes.isSuccess)
        val json = jsonRes.getOrThrow()
        assertTrue(json.contains("\"name\": \"Sarah\""))
        assertTrue(json.contains("\"id\": 1"))
    }

    @Test
    fun testJsonToYaml() {
        val json = """{"service":"nexus","port":8080,"debug":true}"""
        val yamlRes = JsonConverterEngine.jsonToYaml(json)
        assertTrue(yamlRes.isSuccess)
        val yaml = yamlRes.getOrThrow()
        assertTrue(yaml.contains("service: nexus"))
        assertTrue(yaml.contains("port: 8080"))
        assertTrue(yaml.contains("debug: true"))
    }
}
