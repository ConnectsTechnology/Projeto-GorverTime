package lass.govertime.Presidentes;

/**
 * Created by Nailson on 12/05/2018.
 */

public class Pessoa {

    private String nome;
    private String imagem;
    private String fonte;
    private String sobre;
    private String voto;

    public Pessoa() {
    }

    public Pessoa(String nome, String imagem, String fonte, String sobre, String voto) {
        this.nome = nome;
        this.imagem = imagem;
        this.fonte = fonte;
        this.sobre = sobre;
        this.voto = voto;
    }

    public String getVoto() {
        return voto;
    }

    public void setVoto(String voto) {
        this.voto = voto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getSobre() {
        return sobre;
    }

    public void setSobre(String sobre) {
        this.sobre = sobre;
    }
}
