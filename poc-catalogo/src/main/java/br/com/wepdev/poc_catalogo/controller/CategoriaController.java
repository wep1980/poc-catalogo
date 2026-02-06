package br.com.wepdev.poc_catalogo.controller;

import br.com.wepdev.poc_catalogo.dto.CategoriaDTO;
import br.com.wepdev.poc_catalogo.service.CategoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }


    @GetMapping
    public ResponseEntity<Page<CategoriaDTO>> findAll(Pageable pageable) {

        Page<CategoriaDTO> categorias = categoriaService.findAllPaged(pageable);

        return ResponseEntity.ok().body(categorias);
    }

    @GetMapping(value = "/{id}")
    public CategoriaDTO findById(@PathVariable Long id) {

        return categoriaService.findById(id);
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> adicionar(@RequestBody CategoriaDTO categoriaDTO) {
        categoriaDTO = categoriaService.adicionar(categoriaDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(categoriaDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(categoriaDTO);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CategoriaDTO> atualizar(@PathVariable Long id, @RequestBody CategoriaDTO categoriaDTO) {

        categoriaDTO = categoriaService.atualizar(id, categoriaDTO);

        return ResponseEntity.ok().body(categoriaDTO);
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {

        categoriaService.remover(id);

        return ResponseEntity.noContent().build();
    }
}
