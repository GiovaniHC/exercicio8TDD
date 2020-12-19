package br.edu.ifpr.servicos;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.edu.ifpr.entidades.Filme;
import br.edu.ifpr.entidades.Locacao;
import br.edu.ifpr.entidades.Usuario;
import br.edu.ifpr.exceptions.FilmeSemEstoqueException;
import br.edu.ifpr.exceptions.LocacaoServiceException;
import br.edu.ifpr.utils.DataUtils;

public class LocacaoServiceTest {

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException expected = ExpectedException.none();

	private LocacaoService service;

	@Before
	public void setup() {
		// System.out.println("@Before");
		service = new LocacaoService();
	}

	@After
	public void tearDown() {
		// System.out.println("@After");
	}

	@Test
	public void devePermitirAlugarFilme() throws Exception {

		// cenario
		Usuario usuario = new Usuario("Jefferson");

		List<Filme> filmes = Arrays.asList(new Filme("Poderoso Chefão", 5, 4.0), new Filme("A vida é bela", 2, 4.0));

		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterDataComDiferencaDias(0)));

		assertThat(locacao.getValor(), is(not(6.0)));
		assertThat(locacao.getValor(), is(equalTo(8.0)));
	}

	@Test
	public void devePermitirAlugarFilme2() throws Exception {

		// cenario
		Usuario usuario = new Usuario("Jefferson");

		List<Filme> filmes = Arrays.asList(new Filme("Poderoso Chefão", 5, 5.0), new Filme("A vida é bela", 2, 4.0));

		Locacao locacao;

		// acao
		locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(9.0)));
	}

	// METODO 1
	@Test(expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecaoQuandoNaoTiverEstoque() throws FilmeSemEstoqueException, LocacaoServiceException {

		// cenario
		Usuario usuario = new Usuario("Jefferson");

		List<Filme> filmes = Arrays.asList(new Filme("Poderoso Chefão", 0, 5.0), new Filme("A vida é bela", 2, 4.0));

		// acao
		service.alugarFilme(usuario, filmes); // execucao para aqui
	}

	// METODO 2
	@Test
	public void deveLancarExcecaoQuandoNaoTiverEstoque2() throws LocacaoServiceException {

		// cenario
		Usuario usuario = new Usuario("Jefferson");

		List<Filme> filmes = Arrays.asList(new Filme("Poderoso Chefão", 0, 5.0), new Filme("A vida é bela", 2, 4.0));

		// acao
		try {

			service.alugarFilme(usuario, filmes);
			Assert.fail("teste deve lançar uma exceção");

		} catch (FilmeSemEstoqueException e) {

			Assert.assertThat(e.getMessage(), is("filme sem estoque"));
		}

	}

	// METODO 3
	@Test
	public void deveLancarExcecaoQuandoNaoTiverEstoque3() throws FilmeSemEstoqueException, LocacaoServiceException {

		// cenario
		Usuario usuario = new Usuario("Jefferson");

		List<Filme> filmes = Arrays.asList(new Filme("Poderoso Chefão", 0, 5.0), new Filme("A vida é bela", 2, 4.0));

		expected.expect(FilmeSemEstoqueException.class);
		expected.expectMessage("filme sem estoque");

		// acao
		service.alugarFilme(usuario, filmes);
	}

	@Test
	public void deveLancarExcecaoQuandoFilmeEstiverNulo() throws FilmeSemEstoqueException {
		// Cenario
		Usuario usuario = new Usuario("jefferson");

		// acao
		try {
			service.alugarFilme(usuario, null);
			Assert.fail();

		} catch (LocacaoServiceException e) {
			Assert.assertThat(e.getMessage(), is("filme nulo"));
		}
	}

	@Test
	public void dataDeDevolucaoNaoPodeSerNoDomingo() throws Exception {

		// cenario
		Usuario usuario = new Usuario("Jose");

		List<Filme> filmes = Arrays.asList(new Filme("Poderoso Chefão", 5, 5.0), new Filme("A vida é bela", 2, 4.0));

		Calendar data = Calendar.getInstance();
		// setar a data do sistema para realizar o teste com o cenário esperado
		// mes com 11 pois o data.set() tem os meses de 0(janeiro) a 11(dezembro)
		data.set(2020, 11, 19, 14, 30);
		Locacao locacao;

		// acao
		locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		// se o metodo retornar false não é domindo então o teste passa
		assertFalse(DataUtils.verificarDiaSemana(locacao.getDataRetorno(), 1));
	}

	@Test
	public void calculoDescontoNoTerceiroFilme() throws Exception {

		// cenario
		Usuario usuario = new Usuario("Jose");

		List<Filme> filmes = Arrays.asList(new Filme("Poderoso Chefão", 5, 10.0), new Filme("A vida é bela", 2, 10.0),
				new Filme("Velozes e furiosos : Desafio em Tóquio", 5, 10.0));
		Locacao locacao;

		// acao
		locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		// verifica se aplicou 25% de desconto no terceiro filme alugado
		assertEquals(locacao.getValor(), 27.5, 0.00);
	}

	@Test
	public void calculoDescontoNoQuartoFilme() throws Exception {

		// cenario
		Usuario usuario = new Usuario("Jose");

		List<Filme> filmes = Arrays.asList(new Filme("Poderoso Chefão", 5, 10.0), new Filme("A vida é bela", 2, 10.0),
				new Filme("Velozes e furiosos : Desafio em Tóquio", 5, 10.0), new Filme("Batman vs Superman", 3, 10.0));
		Locacao locacao;

		// acao
		locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		// verifica se aplicou 50% de desconto no quarto filme alugado
		assertEquals(locacao.getValor(), 35, 0.00);
	}

	@Test
	public void calculoDescontoNoQuintoFilme() throws Exception {

		// cenario
		Usuario usuario = new Usuario("Jose");

		List<Filme> filmes = Arrays.asList(new Filme("Poderoso Chefão", 5, 10.0), new Filme("A vida é bela", 2, 10.0),
				new Filme("Velozes e furiosos : Desafio em Tóquio", 5, 10.0), new Filme("Batman vs Superman", 3, 10.0),
				new Filme("Tenet", 12, 10.0));
		Locacao locacao;

		// acao
		locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		// verifica se aplicou 75% de desconto no quinto filme alugado
		assertEquals(locacao.getValor(), 42.5, 0.00);
	}

	@Test
	public void calculoDescontoNoSextoFilme() throws Exception {

		// cenario
		Usuario usuario = new Usuario("Jose");

		List<Filme> filmes = Arrays.asList(new Filme("Poderoso Chefão", 5, 10.0), new Filme("A vida é bela", 2, 10.0),
				new Filme("Velozes e furiosos : Desafio em Tóquio", 5, 10.0), new Filme("Batman vs Superman", 3, 10.0),
				new Filme("Tenet", 12, 10.0), new Filme("Venom", 9, 10.0));
		Locacao locacao;

		// acao
		locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		// verifica se aplicou 100% de desconto no sexto filme alugado
		assertEquals(locacao.getValor(), 50, 0.00);
	}
}
