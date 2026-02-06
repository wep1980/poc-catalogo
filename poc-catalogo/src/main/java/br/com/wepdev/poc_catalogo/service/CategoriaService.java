package br.com.wepdev.poc_catalogo.service;

import br.com.wepdev.poc_catalogo.domain.Categoria;
import br.com.wepdev.poc_catalogo.dto.CategoriaDTO;
import br.com.wepdev.poc_catalogo.exception.DatabaseException;
import br.com.wepdev.poc_catalogo.repository.CategoriaRepository;
import br.com.wepdev.poc_catalogo.exception.CategoriaNaoEncontradaException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }


    @Transactional(readOnly = true)
    public Page<CategoriaDTO> findAllPaged(Pageable pageable) {

        Page<Categoria> categorias = categoriaRepository.findAll(pageable);

        return categorias.map(CategoriaDTO::new);
    }

    @Transactional(readOnly = true)
    public CategoriaDTO findById(Long id) {

        Categoria categoria = categoriaRepository.findById(id).orElseThrow( () ->
                new CategoriaNaoEncontradaException("Categoria com id " + id + " não encontrada"));

        return new CategoriaDTO(categoria);
    }

    @Transactional
    public CategoriaDTO adicionar(CategoriaDTO categoriaDTO) {
        Categoria categoria = new Categoria(categoriaDTO.getNome());

        categoria = categoriaRepository.save(categoria);

        return new CategoriaDTO(categoria);
    }

    @Transactional
    public CategoriaDTO atualizar(Long id ,CategoriaDTO categoriaDTO) {
        try {
            Categoria categoria = categoriaRepository.getReferenceById(id);

            categoria.setNome(categoriaDTO.getNome());

            categoria = categoriaRepository.save(categoria);

            return new CategoriaDTO(categoria);

        }catch (EntityNotFoundException e) {
            throw new CategoriaNaoEncontradaException("Categoria com id " + id + " não encontrada");
        }

    }

    @Transactional
    public void remover(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new CategoriaNaoEncontradaException("Categoria com id " + id + " não encontrada");
        }
        try {
            categoriaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Categoria com id " + id + " não pode ser removida, pois está associada a produtos.");
        }
    }

}


