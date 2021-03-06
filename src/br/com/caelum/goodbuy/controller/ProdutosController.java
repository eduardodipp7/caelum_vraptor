package br.com.caelum.goodbuy.controller;

import java.util.List;

import br.com.caelum.goodbuy.infra.ProdutoDao;
import br.com.caelum.goodbuy.modelo.Produto;
import br.com.caelum.goodbuy.modelo.Restrito;
import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.view.Results;

@Resource
public class ProdutosController {

	private final ProdutoDao dao;
	private final Result result;
	private final Validator validator;

	public ProdutosController(ProdutoDao dao, Result result, Validator validator) {
		this.dao = dao;
		this.result = result;
		this.validator = validator;
	}

	@Get("/produtos")
	public void lista() {
		String mensagem = "teste";
		result.include("key", mensagem);
		result.include("produtoList", dao.listaTudo());
	}

	@Restrito
	@Post("/produtos")
	public void adiciona(Produto produto) {
		
		validaFormulario(produto);
		
		dao.salva(produto);
		result.redirectTo(this).lista();
	}

	@Restrito
	@Get("/produtos/novo")
	public void formulario() {
	}

	@Restrito
	@Get("/produtos/{id}")
	public Produto edita(Long id) {
		return dao.getProduto(id);
	}

	@Restrito
	@Put("/produtos/{produto.id}")
	public void altera(Produto produto) {
		
		validaFormulario(produto);
		
		dao.atualiza(produto);
		result.redirectTo(this).lista();
	}

	@Restrito
	@Delete("/produtos/{id}")
	public void remove(Long id) {
		Produto produto = dao.getProduto(id);
		dao.remove(produto);
		result.redirectTo(this).lista();
	}
	
	@Get("/produtos/busca.json")
	public void buscaJson(String q) {
		result.use(Results.json()).withoutRoot().from(dao.busca(q)).exclude("id", "descricao").serialize();
	}
	
	@Get("/produtos/busca")
	public List<Produto> busca(String nome) {
		result.include("nome", nome);
		return dao.busca(nome);
	}
	
	private void validaFormulario(Produto produto) {
		validator.validate(produto);
		validator.onErrorUsePageOf(this).formulario();
	}
}
