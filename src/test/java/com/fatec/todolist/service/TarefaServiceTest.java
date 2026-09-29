package com.fatec.todolist.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.todolist.exception.RegraNegocioException;
import com.fatec.todolist.exception.TarefaNaoEncontradaException;
import com.fatec.todolist.model.entity.Tarefa;
import com.fatec.todolist.model.enums.StatusTarefa;
import com.fatec.todolist.model.repository.TarefaRepository;
import com.fatec.todolist.service.impl.TarefaServiceImpl;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    TarefaRepository repository;

    @InjectMocks
    TarefaServiceImpl service;

    private Tarefa tarefa(String nome) {
        Tarefa t = new Tarefa();
        t.setNome(nome);
        return t;
    }

    @Test
    void criarDefineStatusPendenteQuandoNaoInformado() {
        when(repository.save(any(Tarefa.class))).thenAnswer(i -> i.getArgument(0));
        Tarefa salva = service.criar(tarefa("Estudar"));
        assertEquals(StatusTarefa.PENDENTE, salva.getStatus());
    }

    @Test
    void criarSemNomeLancaExcecao() {
        assertThrows(RegraNegocioException.class, () -> service.criar(tarefa(" ")));
        verify(repository, never()).save(any());
    }

    @Test
    void atualizarAlteraCampos() {
        Tarefa existente = tarefa("Antigo");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Tarefa.class))).thenAnswer(i -> i.getArgument(0));

        Tarefa dados = tarefa("Novo");
        dados.setStatus(StatusTarefa.CONCLUIDA);
        dados.setObservacoes("ok");

        Tarefa r = service.atualizar(1L, dados);
        assertEquals("Novo", r.getNome());
        assertEquals(StatusTarefa.CONCLUIDA, r.getStatus());
        assertEquals("ok", r.getObservacoes());
    }

    @Test
    void atualizarInexistenteLancaExcecao() {
        when(repository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(TarefaNaoEncontradaException.class, () -> service.atualizar(9L, tarefa("X")));
    }

    @Test
    void deletarRemoveTarefa() {
        Tarefa t = tarefa("Del");
        when(repository.findById(1L)).thenReturn(Optional.of(t));
        service.deletar(1L);
        verify(repository).delete(t);
    }

    @Test
    void deletarInexistenteLancaExcecao() {
        when(repository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(TarefaNaoEncontradaException.class, () -> service.deletar(9L));
    }
}
