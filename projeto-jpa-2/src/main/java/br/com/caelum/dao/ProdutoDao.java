package br.com.caelum.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.caelum.model.Loja;
import br.com.caelum.model.Produto;

@Repository
public class ProdutoDao {

	@PersistenceContext
	private EntityManager em;

	public List<Produto> getProdutos() {
		return em.createQuery("from Produto", Produto.class).getResultList();
	}

	public Produto getProduto(Integer id) {
		Produto produto = em.find(Produto.class, id);
		return produto;
	}

	public List<Produto> getProdutos(String nome, Integer categoriaId, Integer lojaId) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Produto> query = criteriaBuilder.createQuery(Produto.class);
		Root<Produto> root = query.from(Produto.class);
		
		Predicate conjuncao = criteriaBuilder.conjunction();


		if (!nome.isEmpty()) {
			Path<String> nomePath = root.<String> get("nome");
			Predicate nomeIgual = criteriaBuilder.like(nomePath, "%"+nome+"%");
			conjuncao = criteriaBuilder.and(nomeIgual);
		}
		if (categoriaId != null) {
			Path<Integer> categoriaPath = root.join("categorias").<Integer> get("id");
			Predicate categoriaIgual = criteriaBuilder.equal(categoriaPath, categoriaId);
			conjuncao = criteriaBuilder.and(conjuncao, categoriaIgual);
		}
		if (lojaId != null) {
			Path<Integer> lojaPath = root.<Loja> get("loja").<Integer> get("id");
			Predicate lojaIgual = criteriaBuilder.equal(lojaPath, lojaId);
			conjuncao = criteriaBuilder.and(conjuncao, lojaIgual);
		}

		query.where(conjuncao);

		TypedQuery<Produto> typedQuery = em.createQuery(query);
		return typedQuery.getResultList();

	}

	public void insere(Produto produto) {
		if (produto.getId() == null)
			em.persist(produto);
		else
			em.merge(produto);
	}

}
