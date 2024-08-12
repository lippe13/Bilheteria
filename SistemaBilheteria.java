import javax.swing.JOptionPane;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

abstract class Evento {
    protected String nome;
    protected Date data;
    protected String hora;
    protected String local;
    protected int quantidadeIngressos;
    protected double precoIngresso;
    protected List<Ingresso> ingressosVendidos = new ArrayList<>();

    public Evento(String nome, Date data, String hora, String local, int quantidadeIngressos, double precoIngresso) {
        this.nome = nome;
        this.data = data;overide
        this.hora = hora;
        this.local = local;
        this.quantidadeIngressos = quantidadeIngressos;
        this.precoIngresso = precoIngresso;
    }

    public String getNome() {
        return nome;
    }

    public Date getData() {
        return data;
    }

    public String getHora() {
        return hora;
    }

    public String getLocal() {
        return local;
    }

    public int getQuantidadeIngressos() {
        return quantidadeIngressos;
    }

    public double getPrecoIngresso() {
        return precoIngresso;
    }

    public int getIngressosDisponiveis() {
        return quantidadeIngressos - ingressosVendidos.size();
    }

    public double getReceita() {
        return ingressosVendidos.stream().mapToDouble(Ingresso::getValor).sum();
    }

    public void venderIngresso(Ingresso ingresso) {
        if (getIngressosDisponiveis() > 0) {
            ingressosVendidos.add(ingresso);
        } else {
            JOptionPane.showMessageDialog(null, "Ingressos esgotados para este evento.");
        }
    }

    public void exibirDetalhes() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JOptionPane.showMessageDialog(null, "Nome: " + nome + "\n" +
                "Data: " + sdf.format(data) + "\n" +
                "Hora: " + hora + "\n" +
                "Local: " + local + "\n" +
                "Ingressos Disponíveis: " + getIngressosDisponiveis() + "\n" +
                "Preço do Ingresso: " + precoIngresso + "\n" +
                "Receita do Evento: " + getReceita());
    }

    public abstract boolean podeVenderIngresso(Ingresso ingresso);
}

class Filme extends Evento {
    private int ingressosMeiaEntradaDisponiveis;

    public Filme(String nome, Date data, String hora, String local) {
        super(nome, data, hora, local, 200, 20.0);
        this.ingressosMeiaEntradaDisponiveis = (int) (0.2 * 200); // 20% dos ingressos
    }

    public boolean podeVenderIngresso(Ingresso ingresso) {
        if (ingresso instanceof MeiaEntrada) {
            return ingressosMeiaEntradaDisponiveis > 0;
        }
        return true;
    }

    public void venderIngresso(Ingresso ingresso) {
        if (ingresso instanceof MeiaEntrada) {
            if (ingressosMeiaEntradaDisponiveis > 0) {
                ingressosMeiaEntradaDisponiveis--;
                super.venderIngresso(ingresso);
            } else {
                JOptionPane.showMessageDialog(null, "Esse tipo de ingresso se esgotou, escolha outro.");
                return;
            }
        } else {
            super.venderIngresso(ingresso);
        }
    }
}

class Teatro extends Evento {
    private int ingressosMeiaEntradaDisponiveis;
    private int ingressosVIPDisponiveis;

    public Teatro(String nome, Date data, String hora, String local) {
        super(nome, data, hora, local, 250, 30.0);
        this.ingressosMeiaEntradaDisponiveis = (int) (0.2 * 250); // 20% dos ingressos
        this.ingressosVIPDisponiveis = (int) (0.1 * 250); // 10% dos ingressos
    }

    public boolean podeVenderIngresso(Ingresso ingresso) {
        if (ingresso instanceof MeiaEntrada) {
            return ingressosMeiaEntradaDisponiveis > 0;
        } else if (ingresso instanceof VIP) {
            return ingressosVIPDisponiveis > 0;
        }
        return true;
    }

    public void venderIngresso(Ingresso ingresso) {
        if (ingresso instanceof MeiaEntrada) {
            if (ingressosMeiaEntradaDisponiveis > 0) {
                ingressosMeiaEntradaDisponiveis--;
                super.venderIngresso(ingresso);
            } else {
                JOptionPane.showMessageDialog(null, "Esse tipo de ingresso se esgotou, escolha outro.");
                return;
            }
        } else if (ingresso instanceof VIP) {
            if (ingressosVIPDisponiveis > 0) {
                ingressosVIPDisponiveis--;
                super.venderIngresso(ingresso);
            } else {
                JOptionPane.showMessageDialog(null, "Esse tipo de ingresso se esgotou, escolha outro.");
                return;
            }
        } else {
            super.venderIngresso(ingresso);
        }
    }
}

class Show extends Evento {
    private int ingressosVIPDisponiveis;

    public Show(String nome, Date data, String hora, String local) {
        super(nome, data, hora, local, 150, 50.0);
        this.ingressosVIPDisponiveis = (int) (0.1 * 150); // 10% dos ingressos
    }

    public boolean podeVenderIngresso(Ingresso ingresso) {
        if (ingresso instanceof VIP) {
            return ingressosVIPDisponiveis > 0;
        }
        return true;
    }

    public void venderIngresso(Ingresso ingresso) {
        if (ingresso instanceof VIP) {
            if (ingressosVIPDisponiveis > 0) {
                ingressosVIPDisponiveis--;
                super.venderIngresso(ingresso);
            } else {
                JOptionPane.showMessageDialog(null, "Esse tipo de ingresso se esgotou, escolha outro.");
                return;
            }
        } else {
            super.venderIngresso(ingresso);
        }
    }
}

abstract class Ingresso {
    protected double valor;

    public Ingresso(double valor) {
        this.valor = valor;
    }

    public double getValor() {
        return valor;
    }
}

class Normal extends Ingresso {
    public Normal(double valor) {
        super(valor);
    }
}

class VIP extends Ingresso {
    public VIP(double valor) {
        super(valor);
    }
}

class MeiaEntrada extends Ingresso {
    public MeiaEntrada(double valor) {
        super(valor);
    }
}

public class SistemaBilheteria {
    private static List<Evento> eventos = new ArrayList<>();
    private static List<String> extratoCompras = new ArrayList<>();

    public static void main(String[] args) {
        inicializarEventos();
        boolean primeiraExecucao = true;

        while (true) {
            if (primeiraExecucao) {
                JOptionPane.showMessageDialog(null, "BEM VINDO A BILHETERIA!");
                primeiraExecucao = false;
            }

            int tipoEvento = Integer.parseInt(JOptionPane.showInputDialog(null,
                    "Escolha um tipo de evento:\n" +
                            "[1] - Cinema\n" +
                            "[2] - Teatro\n" +
                            "[3] - Show\n" +
                            "[0] - Sair\n" +
                            "[9] - Imprimir Extrato"));

            switch (tipoEvento) {
                case 0:
                    JOptionPane.showMessageDialog(null, "Obrigado por usar o sistema de bilheteira!");
                    return;
                case 9:
                    imprimirExtrato();
                    break;
                case 1:
                case 2:
                case 3:
                    selecionarEvento(tipoEvento);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida, tente novamente.");
                    break;
            }
        }
    }

    private static void inicializarEventos() {
        eventos.add(new Filme("Vingadores", new Date(), "19:00", "Sala 1"));
        eventos.add(new Filme("Thor", new Date(), "21:00", "Sala 2"));
        eventos.add(new Filme("Deadpool", new Date(), "22:00", "Sala 3"));

        eventos.add(new Teatro("Mágico de Oz", new Date(), "20:00", "Teatro A"));
        eventos.add(new Teatro("Turma da Mônica", new Date(), "18:00", "Teatro B"));
        eventos.add(new Teatro("Drama", new Date(), "21:00", "Teatro C"));

        eventos.add(new Show("Kiss", new Date(), "22:00", "Arena X"));
        eventos.add(new Show("Kevin", new Date(), "20:00", "Arena Y"));
        eventos.add(new Show("Alok", new Date(), "23:00", "Arena Z"));
    }

    private static void selecionarEvento(int tipoEvento) {
        String[] opcoes;
        switch (tipoEvento) {
            case 1:
                opcoes = new String[]{"Vingadores", "Thor", "Deadpool"};
                break;
            case 2:
                opcoes = new String[]{"Mágico de Oz", "Turma da Mônica", "Drama"};
                break;
            case 3:
                opcoes = new String[]{"Kiss", "Kevin", "Alok"};
                break;
            default:
                return;
        }

        String eventoEscolhido = (String) JOptionPane.showInputDialog(null,
                "Escolha o evento:\n", "Seleção de Evento",
                JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);

        Evento evento = eventos.stream()
                .filter(e -> e.getNome().equals(eventoEscolhido))
                .findFirst()
                .orElse(null);

        if (evento != null) {
            evento.exibirDetalhes();
            selecionarTipoIngresso(evento);
        }
    }

    private static void selecionarTipoIngresso(Evento evento) {
        String[] ingressos;
        if (evento instanceof Filme) {
            ingressos = new String[]{"Normal", "Meia-Entrada"};
        } else {
            ingressos = new String[]{"Normal", "Meia-Entrada", "VIP"};
        }

        String tipoIngressoEscolhido = (String) JOptionPane.showInputDialog(null,
                "Escolha o tipo de ingresso:\n", "Seleção de Ingresso",
                JOptionPane.QUESTION_MESSAGE, null, ingressos, ingressos[0]);

        Ingresso ingresso = criarIngresso(tipoIngressoEscolhido, evento.getPrecoIngresso());
        if (ingresso != null && evento.podeVenderIngresso(ingresso)) {
            evento.venderIngresso(ingresso);
            JOptionPane.showMessageDialog(null, "Compra realizada com sucesso!");
            extratoCompras.add("Evento: " + evento.getNome() + ", Tipo de Ingresso: " + tipoIngressoEscolhido +
                    ", Valor: " + ingresso.getValor() + ", Data: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        } else {
            JOptionPane.showMessageDialog(null, "Tipo de ingresso não disponível ou esgotado.");
        }
    }

    private static Ingresso criarIngresso(String tipo, double valor) {
        switch (tipo) {
            case "Normal":
                return new Normal(valor);
            case "Meia-Entrada":
                return new MeiaEntrada(valor / 2);
            case "VIP":
                return new VIP(valor + 20.0); // Adicionando um valor extra para VIP
            default:
                return null;
        }
    }

    private static void imprimirExtrato() {
        if (extratoCompras.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhuma compra realizada até o momento.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String registro : extratoCompras) {
                sb.append(registro).append("\n");
            }
            JOptionPane.showMessageDialog(null, sb.toString());
        }
    }
}
