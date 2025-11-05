package com.tarrific.backend.controller;

import com.tarrific.backend.model.HsCode;
import com.tarrific.backend.repository.HsCodeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hscodes")
@CrossOrigin(origins = "http://localhost:3000")
public class HsCodeController {

    private final HsCodeRepository repository;

    public HsCodeController(HsCodeRepository repository) {
        this.repository = repository;
    }

    // READ all HS codes
    @GetMapping
    public List<HsCode> getAll() {
        return repository.findAll();
    }

    // READ one HS code
    @GetMapping("/{hsCode}")
    public ResponseEntity<HsCode> getById(@PathVariable String hsCode) {
        return repository.findById(hsCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE a new HS code
    @PostMapping
    public HsCode create(@RequestBody HsCode hsCode) {
        return repository.save(hsCode);
    }

    // UPDATE an existing HS code
    @PutMapping("/{hsCode}")
    public ResponseEntity<HsCode> update(@PathVariable String hsCode, @RequestBody HsCode updated) {
        return repository.findById(hsCode)
                .map(existing -> {
                    existing.setDescription(updated.getDescription());
                    existing.setChapter(updated.getChapter());
                    existing.setHeading(updated.getHeading());
                    existing.setSubheading(updated.getSubheading());
                    repository.save(existing);
                    return ResponseEntity.ok(existing);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE an HS code
    @DeleteMapping("/{hsCode}")
    public ResponseEntity<Void> delete(@PathVariable String hsCode) {
        if (!repository.existsById(hsCode)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(hsCode);
        return ResponseEntity.noContent().build();
    }
}
