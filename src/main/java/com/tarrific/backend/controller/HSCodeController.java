package com.tarrific.backend.controller;

import com.tarrific.backend.model.HSCode;
import com.tarrific.backend.repository.HSCodeRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/hscodes")
public class HSCodeController {

    private final HSCodeRepository repository;

    public HSCodeController(HSCodeRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<HSCode> all() {
        return repository.findAll();
    }

    @PostMapping
    public HSCode create(@RequestBody HSCode hsCode) {
        return repository.save(hsCode);
    }
}
