package com.rest.cucumber.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;

@RestController
@RequestMapping("/home")
public class HomeController {
    private HashMap<Integer, JsonNode> database = new HashMap<>();
    private JsonNode get(String key, String json) {
        if (key == null || key.isEmpty()) {
            key = "-1";
        }
        return get(Integer.valueOf(key), json);
    }
    public JsonNode get(Integer key, String json) {
        if (key.intValue() == -1) {
            if (database.isEmpty()) {
                key = 1;
            } else {
                key = Collections.max(database.keySet()) + 1;
            }
        }
        JsonNode existing = database.get(key);
        if (existing == null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                if (json == null || json.isEmpty()) {
                    json = "{ \"key\": \"" +key + "\"}";
                }
                existing = mapper.readTree(json);
                ((ObjectNode)existing).put("key", key);
                database.put(key, existing);
            } catch (Exception e) {
                e.printStackTrace();
                // invalid, gobble exeption
            }
        }
        return existing;
    }
    @GetMapping("/welcome")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Welcome to REST Assured testing application!");
    }

    @GetMapping("/get/{some}/{key}")
    public ResponseEntity<JsonResponse> doGetKeyPath(@PathVariable(name="some") String some, @PathVariable(name="key") String key, @RequestBody(required=false) String json) throws Exception{
        return ResponseEntity.ok(new JsonResponse("get", some, key, get(key, json)));
    }

    @GetMapping("/get/{some}")
    public ResponseEntity<String> doGet(@PathVariable(name="some") String some, @RequestParam(name="key") String key, @RequestBody(required=false) String json) throws Exception{
        return getStringResponseEntity("get", some, key, get(key, json));
    }

    public record JsonResponse(String httpType, String path, String key, JsonNode body){}

    @GetMapping("/get/{some}/object")
    public ResponseEntity<JsonResponse> doGetRetObj(@PathVariable(name="some") String some, @RequestParam(name="key") String key, @RequestBody String json) throws Exception{
        return ResponseEntity.ok(new JsonResponse("get", some, key, get(key, json)));
    }

    @GetMapping("/get/{some}/error")
    public ResponseEntity<JsonResponse> doGetRetRemovesNode(@PathVariable(name="some") String some, @RequestParam(name="key") String key, @RequestBody String json) throws Exception{
        JsonNode jsonNode = new ObjectMapper().readTree(json);
        JsonNode arr = jsonNode.get(key);
        if (arr.isArray()) {
            ((ArrayNode)arr).remove(0);
        }
        return ResponseEntity.ok(new JsonResponse("get", some, key, jsonNode));
    }

    @PutMapping("/put/{some}")
    public ResponseEntity<String> doPut(@PathVariable(name="some") String some, @RequestParam(name="key") String key, @RequestBody String json) throws Exception{
        return getStringResponseEntity("put", some, key, get(key, json));
    }

    @PostMapping("/post/{some}")
    public ResponseEntity<JsonResponse> doPost(@PathVariable(name="some") String some, @RequestParam(name="key", required=false) String key, @RequestBody String json) throws Exception{
        JsonNode jsonNode = get(key, json);
        if (key == null || key.isEmpty()) {
            key = String.valueOf(jsonNode.get("key"));
        }
        return ResponseEntity.ok(new JsonResponse("post", some, key, jsonNode));
    }

    @PatchMapping("/patch/{some}")
    public ResponseEntity<String> doPatch(@PathVariable(name="some") String some, @RequestParam(name="key") String key, @RequestBody String json) throws Exception{
        return getStringResponseEntity("patch", some, key, get(key, json));
    }

    @DeleteMapping("/delete/{some}")
    public ResponseEntity<String> doDelete(@PathVariable(name="some") String some, @RequestParam(name="key") String key, @RequestBody String json) throws Exception{
        database.remove(key);
        return getStringResponseEntity("delete", some, key, json);
    }

    private ResponseEntity<String> getStringResponseEntity(String httpType, String some, String key, String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return getStringResponseEntity(httpType, some, key, mapper.readTree(json));
    }

    private ResponseEntity<String> getStringResponseEntity(String httpType, String some, String key, JsonNode json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode on = mapper.createObjectNode();
        on.put("httpType", httpType);
        on.put("path", some);
        on.put("key", key);
        on.set("body", json);
        return ResponseEntity.ok(mapper.writeValueAsString(on));
    }
}