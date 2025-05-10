package com.siemens.internship;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllItems() throws Exception {
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateItem_valid() throws Exception {
        Item item = new Item(null, "Test", "desc", "NEW", "test@test.com");

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testCreateItem_invalidEmail() throws Exception {
        Item item = new Item(null, "Test", "desc", "NEW", "invalid");

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateItem_notFound() throws Exception {
        Item item = new Item(null, "Update", "desc", "NEW", "u@u.com");

        mockMvc.perform(put("/api/items/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteItem_notFound() throws Exception {
        mockMvc.perform(delete("/api/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testProcessItems() throws Exception {
        mockMvc.perform(get("/api/items/process"))
                .andExpect(status().isOk());
    }
    @Test
    void testUpdateItem_invalidInput_returnsBadRequest() throws Exception {
        Item item = new Item(null, "", "", "NEW", "not-an-email");

        mockMvc.perform(put("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testDeleteItem_existingItem() throws Exception {
        Item item = new Item(null, "ToDelete", "desc", "NEW", "delete@me.com");

        String json = objectMapper.writeValueAsString(item);
        String location = mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long createdId = objectMapper.readValue(location, Item.class).getId();
        mockMvc.perform(delete("/api/items/" + createdId))
                .andExpect(status().isNoContent());
    }
    @Test
    void testUpdateItem_existingItem_success() throws Exception {
        Item item = new Item(null, "Original", "desc", "NEW", "update@ok.com");

        String json = objectMapper.writeValueAsString(item);
        String response = mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readValue(response, Item.class).getId();

        Item updatedItem = new Item(null, "Updated", "new desc", "PROCESSED", "update@ok.com");

        mockMvc.perform(put("/api/items/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }



}
