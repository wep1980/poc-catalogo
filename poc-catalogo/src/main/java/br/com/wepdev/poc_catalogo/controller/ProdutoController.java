package br.com.wepdev.poc_catalogo.controller;

import br.com.wepdev.poc_catalogo.dto.ProdutoDTO;
import br.com.wepdev.poc_catalogo.service.ProdutoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }


    @GetMapping
    public ResponseEntity<Page<ProdutoDTO>> findAll(Pageable pageable) {

        Page<ProdutoDTO> produtos = produtoService.findAllPaged(pageable);

        return ResponseEntity.ok().body(produtos);
    }

    @GetMapping(value = "/{id}")
    public ProdutoDTO findById(@PathVariable Long id) {

        return produtoService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ProdutoDTO> adicionar(@RequestBody ProdutoDTO produtoDTO) {
        produtoDTO = produtoService.adicionar(produtoDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(produtoDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(produtoDTO);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProdutoDTO> atualizar(@PathVariable Long id, @RequestBody ProdutoDTO produtoDTO) {

        produtoDTO = produtoService.atualizar(id, produtoDTO);

        return ResponseEntity.ok().body(produtoDTO);
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {

        produtoService.remover(id);

        return ResponseEntity.noContent().build();
    }
}
