package org.harvanir.batch.springbatch.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Harvan Irsyadi
 */
@RestController
public class ItemController {

    @GetMapping
    public String findAll() {
        return "OK";
    }
}