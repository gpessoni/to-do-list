package com.fatec.todolist.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TarefaControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    private long criar(String json) throws Exception {
        MvcResult r = mvc.perform(post("/api/tarefas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andReturn();
        return mapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void criaTarefaComStatusPadraoEDatas() throws Exception {
        mvc.perform(post("/api/tarefas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Estudar\",\"descricao\":\"Spring\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.dataCriacao").exists())
                .andExpect(jsonPath("$.dataAtualizacao").exists());
    }

    @Test
    void naoCriaSemNome() throws Exception {
        mvc.perform(post("/api/tarefas").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void alteraTarefa() throws Exception {
        long id = criar("{\"nome\":\"A\"}");
        mvc.perform(put("/api/tarefas/" + id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"B\",\"status\":\"CONCLUIDA\",\"observacoes\":\"feito\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("B"))
                .andExpect(jsonPath("$.status").value("CONCLUIDA"))
                .andExpect(jsonPath("$.observacoes").value("feito"));
    }

    @Test
    void deletaTarefa() throws Exception {
        long id = criar("{\"nome\":\"A\"}");
        mvc.perform(delete("/api/tarefas/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/tarefas/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void listaTarefas() throws Exception {
        criar("{\"nome\":\"A\"}");
        mvc.perform(get("/api/tarefas")).andExpect(status().isOk()).andExpect(jsonPath("$[0].nome").value("A"));
    }

    @Test
    void alterarInexistenteRetorna404() throws Exception {
        mvc.perform(put("/api/tarefas/9999").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"X\"}"))
                .andExpect(status().isNotFound());
    }
}
