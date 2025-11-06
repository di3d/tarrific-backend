package com.tarrific.backend.controller;

import com.tarrific.backend.model.HsCode;
import com.tarrific.backend.repository.HsCodeRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/hscodes")
public class HsCodeController {
    private final HsCodeRepository hsCodeRepository;

    public HsCodeController(HsCodeRepository hsCodeRepository) {
        this.hsCodeRepository = hsCodeRepository;
    }

    @GetMapping
    public List<HsCode> getAll() {
        return hsCodeRepository.findAll(); // returns List<HsCode>
    }

    @GetMapping("/{code}")
    public HsCode getByCode(@PathVariable String code) {
        return hsCodeRepository.findById(code).orElse(null);
    }

    @PostMapping
    public HsCode create(@RequestBody HsCode hs) {
        return hsCodeRepository.save(hs);
    }

    @PutMapping("/{code}")
    public HsCode update(@PathVariable String code, @RequestBody HsCode hs) {
        hs.setHsCode(code);
        return hsCodeRepository.save(hs);
    }

    @DeleteMapping("/{code}")
    public void delete(@PathVariable String code) {
        hsCodeRepository.deleteById(code);
    }
}
