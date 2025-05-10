package com.siemens.internship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        List<Item> items = List.of(new Item(1L, "Test", "desc", "NEW", "a@a.com"));
        when(itemRepository.findAll()).thenReturn(items);

        List<Item> result = itemService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void testFindById_existing() {
        Item item = new Item(1L, "Name", "desc", "NEW", "b@b.com");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Optional<Item> result = itemService.findById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void testSave() {
        Item item = new Item(null, "Name", "desc", "NEW", "c@c.com");
        when(itemRepository.save(item)).thenReturn(new Item(1L, "Name", "desc", "NEW", "c@c.com"));

        Item saved = itemService.save(item);
        assertNotNull(saved.getId());
    }

    @Test
    void testDeleteById() {
        itemService.deleteById(1L);
        verify(itemRepository).deleteById(1L);
    }

    @Test
    void testProcessItemsAsync() throws Exception {
        when(itemRepository.findAllIds()).thenReturn(List.of(1L));
        Item item = new Item(1L, "Test", "desc", "NEW", "x@x.com");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        var result = itemService.processItemsAsync().get();
        assertEquals(1, result.size());
        assertEquals("PROCESSED", result.get(0).getStatus());
    }

    @Test
    void testProcessItemsAsync_itemNotFound() throws Exception {
        when(itemRepository.findAllIds()).thenReturn(List.of(99L));
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        List<Item> result = itemService.processItemsAsync().get();
        assertTrue(result.isEmpty());
    }


}
