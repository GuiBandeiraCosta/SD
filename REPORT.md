# Entrega 3 SD - Relatório

### Alunos

    Filipe Henriques    95573
    Guilherme Costa     95586
    Diogo Adegas        96854

#

Para esta fase do projeto, decidimos utilizar uma solução baseada em **Timestamps**.

Dado a dimensão do problema entendemos que uma solução à base de relógios vetoriais não era justificada. Isto por dois motivos: Em primeiro lugar, a probabilidade dos relogios temporais globais estarem dessicronizados era demasiado baixa e como a densidade de comandos é relativamente pequena  é possivel guardar uma lista com as timestamps de cada comando sem qualquer.

Portanto para esta solução assumimos que, como os nossos Timestamps são obtidos através da data atual do computador (ambos os servidores correm em localhost), os relógios dos dois servidores estão sincronizados.


Dito isto, criamos um timestamp no ClassDefinitions.proto que contém uma String Time que corresponde a Data que o pedido foi feito (usámos LocalDateTime) e uma String command que corresponde ao nome do pedido mais atributos,(ex. "enroll *studentID* *studentName*").

Com esta estrutura criada, armanezamos todos Timestamps de um server dentro do seu estado numa lista que só inclui os comandos geradores de conflitos, nomeadamente o ***enroll*, *cancelEnrollments*,*openEnrollments* e *closeEnrollments***, e quando recebemos um pedido de propagateState, juntamos as listas de Timestamps (original e recebida) e ordenamos por ordem cronológica.

Através da lista ordenada, vemos primeiro qual foi a ultima capacidade a ter sido definida e utilizamo-la como padrão para a quantidade de alunos que podemos por.

Depois disto, executamos os comandos por ordem cronológica de forma a garantir que um *enroll* só pode ser ser feito se na altura em que se inscreveu a turma estava aberta e caso contrário (cenário em que server P tem lista fechada e server S tem lista aberta) passamos esse aluno para a lista dos discarded. 

Garantimos ainda que os alunos são posto por ordem cronologica de forma a que se um aluno se tiver inscrito depois da capacidade ter ficado cheia apenas esse aluno é que não é inscrito e não outro ao acaso.

Por exemplo, sendo a última abertura de turmas para 30 alunos, inscrevemos os primeiros 30 da lista ordenada de Timestamps (respeitando a lógica normal das inscrições).

Garantimos também que se um *cancelEnrollment* acontecer depois de um aluno se inscrever esse aluno é de facto cancelado.

No geral, o nosso projeto consegue manter coerência de estados para os dois servidores.

