package com.siemens.internship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    /**
     * @return all items
     * @returntype List<Item>
     */
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    /**
     *
     * @param id
     * @paramtype Long
     * @return item with that id
     * @returntype Optional<Item>
     */
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    /**
     *
     * @param item
     * @paramtype Item
     * @return saved item
     * @returntype Item
     */
    public Item save(Item item) {
        return itemRepository.save(item);
    }

    /**
     *
     * @param id
     * @paramtype Long
     * deletes item with that id
     */
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }


    /**
     * Your Tasks
     * Identify all concurrency and asynchronous programming issues in the code
     * Fix the implementation to ensure:
     * All items are properly processed before the CompletableFuture completes
     * Thread safety for all shared state
     * Proper error handling and propagation
     * Efficient use of system resources
     * Correct use of Spring's @Async annotation
     * Add appropriate comments explaining your changes and why they fix the issues
     * Write a brief explanation of what was wrong with the original implementation
     *
     * Hints
     * Consider how CompletableFuture composition can help coordinate multiple async operations
     * Think about appropriate thread-safe collections
     * Examine how errors are handled and propagated
     * Consider the interaction between Spring's @Async and CompletableFuture
     */

    /**
     * Process async all items:
     * - mark them as PROCESSED
     * - save in bd
     * - wait until all tasks are finished
     *
     * @return all processed items
     * @returntype CompletableFuture<List<Item>>
     */
    @Async
    public CompletableFuture<List<Item>> processItemsAsync() {
        // Get all item IDs from the database
        List<Long> ids = itemRepository.findAllIds();

        // Prepare async processing tasks for each ID
        List<CompletableFuture<Item>> taskList = new ArrayList<>();

        for (Long id : ids) {
            CompletableFuture<Item> task = CompletableFuture.supplyAsync(() -> {
                Item item = itemRepository.findById(id).orElse(null);
                if (item == null) return null;

                item.setStatus("PROCESSED");
                return itemRepository.save(item);
            }, executor);

            taskList.add(task);
        }

        // Wait for all tasks to complete and collect the results
        return CompletableFuture
                .allOf(taskList.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<Item> processed = new ArrayList<>();
                    for (CompletableFuture<Item> future : taskList) {
                        try {
                            Item result = future.get();
                            if (result != null) {
                                processed.add(result);
                            }
                        } catch (Exception e) {
                            System.err.println("Error processing item: " + e.getMessage());
                        }
                    }
                    return processed;
                });
    }


}

