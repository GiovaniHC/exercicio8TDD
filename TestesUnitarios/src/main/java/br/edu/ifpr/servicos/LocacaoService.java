package br.edu.ifpr.servicos;

import static br.edu.ifpr.utils.DataUtils.adicionarDias;

import java.util.Date;
import java.util.List;

import br.edu.ifpr.entidades.Filme;
import br.edu.ifpr.entidades.Locacao;
import br.edu.ifpr.entidades.Usuario;
import br.edu.ifpr.exceptions.FilmeSemEstoqueException;
import br.edu.ifpr.exceptions.LocacaoServiceException;
import br.edu.ifpr.utils.DataUtils;



public class LocacaoService {
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocacaoServiceException {
		
		if(filmes == null) {
			throw new LocacaoServiceException("filme nulo");
		}
		
		if(usuario == null) {
			throw new LocacaoServiceException("usuario nulo");
		}
		
		for (Filme filme : filmes) {
			if (filme.getEstoque() == 0) {
				throw new FilmeSemEstoqueException("filme sem estoque");
			}
		}
		
		
		Locacao locacao = new Locacao();
		Double precoLocacao = 0.0;

		for (Filme filme : filmes) {
			locacao.getFilmes().add(filme);
		}
		
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		
		int posicao = 0;
		//verificação da quantidade
		if(filmes.size()==3) {
			//aplicar 25% de desconto no terceiro filme
			for (Filme filme : filmes) {
				posicao++;
				if(posicao == 3) {
					filme.setPrecoLocacao(filme.getPrecoLocacao() - (filme.getPrecoLocacao() * 0.25)); 
				}
			}
			
		}else if(filmes.size()==4) {
			//aplicar 50% de desconto no quarto filme
			for (Filme filme : filmes) {
				posicao++;
				if(posicao == 4) {
					filme.setPrecoLocacao(filme.getPrecoLocacao() - (filme.getPrecoLocacao() * 0.50)); 
				}
			}
			
		}else if(filmes.size()==5) {
			//aplicar 75% de desconto no quinto filme
			for (Filme filme : filmes) {
				posicao++;
				if(posicao == 5) {
					filme.setPrecoLocacao(filme.getPrecoLocacao() - (filme.getPrecoLocacao() * 0.75)); 
				}
			}
		}else if(filmes.size()==6) {
			//aplicar 100% de desconto no sexto filme
			for (Filme filme : filmes) {
				posicao++;
				if(posicao == 5) {
					filme.setPrecoLocacao(filme.getPrecoLocacao() - (filme.getPrecoLocacao())); 
				}
			}
		}
		
		for (Filme filme : filmes) {
			precoLocacao += filme.getPrecoLocacao();
		}
		
		locacao.setValor(precoLocacao);

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		
		//verifica se a data de retorno caira no domingo, se sim acrescenta mais um dia
		if(DataUtils.verificarDiaSemana(dataEntrega, 1)) {
			locacao.setDataRetorno(adicionarDias(dataEntrega, 1));}
		else {
			locacao.setDataRetorno(dataEntrega);}
		
		//Salvando a locacao...	
		//TODO adicionar método para salvar
		return locacao;
	}

	public static void main(String[] args) {
		
	}
}