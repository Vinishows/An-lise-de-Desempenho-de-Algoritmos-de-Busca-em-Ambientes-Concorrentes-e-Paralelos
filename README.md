# Análise comparativa de algoritmos com uso de paralelismo 
Trabalho de Av3 da cadeira de Computação Paralela

## Resumo 
Este trabalho investiga o desempenho de diferentes estratégias para busca de palavras em texto, empregando métodos seriais e paralelos implementados em Java. São analisadas três abordagens principais: um método sequencial na CPU, um método paralelo na CPU utilizando múltiplos núcleos, e uma abordagem massivamente paralela na GPU por meio de OpenCL. A metodologia considera variações nos tamanhos de texto e configurações de hardware para examinar como diferentes paradigmas de processamento afetam a eficiência e escalabilidade. A análise visa fornecer uma visão abrangente sobre a aplicabilidade e os compromissos das técnicas seriais e paralelas para cenários de computação intensiva.

## Introdução

Para abordar a crescente demanda por eficiência computacional em diversas aplicações, este trabalho explora o desempenho de diferentes estratégias de busca de palavras em texto, utilizando métodos seriais e paralelos implementados em Java. A análise considera três abordagens distintas: uma versão serial na CPU, uma versão paralela na CPU com threads e uma versão paralela na GPU usando OpenCL. A escolha desses métodos visa capturar nuances de desempenho em diferentes arquiteturas e configurações de processamento.

A abordagem proposta integra experimentação prática e análise quantitativa, utilizando:

- **Método Serial na CPU (SerialCPU):** Realiza a busca de forma sequencial, iterando palavra por palavra em um único núcleo da CPU.
- **Método Paralelo na CPU (ParallelCPU):** Divide o processamento entre múltiplos núcleos da CPU, aproveitando pools de threads para acelerar a busca.
- **Método Paralelo na GPU (ParallelGPU):** Alavanca o processamento massivamente paralelo da GPU utilizando OpenCL, otimizando tarefas que envolvem grandes volumes de dados.

A metodologia considera variações nos tamanhos dos textos e ajustes nas configurações de hardware, incluindo o número de núcleos disponíveis, para explorar o impacto dessas variáveis no desempenho. Por fim, gráficos e relatórios serão gerados para sintetizar os resultados e facilitar a interpretação das tendências observadas. Essa análise visa fornecer uma compreensão prática e teórica sobre o uso eficiente de diferentes paradigmas de programação paralela e serial para problemas comuns de computação intensiva.

## Metodologia

### Método Serial na CPU (SerialCPU)
O método SerialCPU realiza a contagem de palavras de forma sequencial. O arquivo de texto é lido linha por linha, e cada linha é dividida em palavras, padronizadas para letras minúsculas. As palavras são comparadas diretamente com a palavra-alvo, incrementando a contagem sempre que ocorre uma correspondência.

Essa abordagem utiliza um único núcleo da CPU e será utilizada como referência para comparação com os métodos paralelos. O desempenho será avaliado em termos de tempo de execução e precisão, especialmente em diferentes tamanhos de textos, para analisar a eficiência relativa do processamento sequencial.

### Método Paralelo na CPU (ParallelCPU)
O método ParallelCPU utiliza threads para processar as linhas de um texto em paralelo, aumentando a eficiência. Cada linha é separada em palavras, que são comparadas com a palavra-alvo. As contagens parciais feitas por cada thread são coletadas usando objetos `Future` e somadas para obter o total de ocorrências.

A abordagem explora os núcleos disponíveis da CPU para reduzir o tempo de execução. O desempenho será avaliado variando o número de threads e o tamanho dos textos, analisando o impacto dessas condições no tempo de execução e na eficiência do paralelismo.

### Método Paralelo na GPU (ParallelGPU)
O método ParallelGPU utiliza OpenCL para realizar a contagem de palavras de forma massivamente paralela na GPU. O texto é carregado em memória, convertido em um array de bytes, e os dados são enviados para buffers na GPU. Um kernel OpenCL, implementado em um arquivo externo, é responsável por comparar cada sequência de bytes do texto com a palavra-alvo, registrando as ocorrências.

O kernel é executado com um _global work size_ igual ao tamanho do texto, permitindo que cada posição do texto seja processada simultaneamente. Os resultados são coletados de um buffer de saída e somados para obter o total de ocorrências.

Essa abordagem explora o alto grau de paralelismo das GPUs para acelerar a busca em textos grandes. O desempenho será avaliado considerando o tempo de execução e a escalabilidade, especialmente em comparação com os métodos serial e paralelo na CPU, destacando as vantagens e limitações do processamento em GPU.

## Resultados e Discussão
A seguir é observado o resultado da comparação entre os algoritmos, por meio da escolha da palavra "word" e seu respectivo gráfico. 
![Resultado CSV](https://github.com/user-attachments/assets/89babf37-9a5a-493c-ac54-87c7d0aac955)
![Gráfico Resultados](https://github.com/user-attachments/assets/31fb7459-fde7-4511-ad4d-55152efdcc62)

Dessa forma, os resultados obtidos mostram diferenças significativas no desempenho dos três métodos em termos de tempo de execução e precisão, dependendo do tamanho e conteúdo das amostras de texto. O método SerialCPU apresentou desempenho consistente, mas mais lento em comparação ao ParallelCPU em todas as amostras, com uma diferença de tempo especialmente evidente em textos maiores, como Moby Dick. O ParallelCPU, aproveitando o uso de múltiplos núcleos, demonstrou melhor desempenho, sendo o mais rápido em todas as execuções, particularmente na amostra Moby Dick, onde alcançou tempos quatro vezes menores que o método serial.

Por outro lado, o ParallelGPU apresentou tempos de execução significativamente maiores, evidenciando o custo de transferência de dados entre a CPU e a GPU, além da sobrecarga associada ao uso do kernel OpenCL para tarefas relativamente pequenas. Este método também apresentou discrepâncias na contagem de ocorrências, como no caso de Moby Dick, indicando possíveis problemas na precisão do processamento paralelo na GPU.

Em síntese, o ParallelCPU mostrou-se a abordagem mais eficiente e confiável para as amostras avaliadas, enquanto o ParallelGPU pode ser mais vantajoso em cenários com dados muito maiores, onde a vantagem do paralelismo massivo compense a sobrecarga inicial. Os resultados destacam a importância de alinhar o método ao contexto e à natureza do problema para maximizar desempenho e precisão.

## Conclusão 
Por fim, ressalta-se que os resultados mostraram que o ParallelCPU foi a solução mais eficiente e confiável, apresentando tempos de execução significativamente menores em comparação ao SerialCPU, especialmente em textos maiores. Já o ParallelGPU, apesar de explorar o alto paralelismo das GPUs, demonstrou limitações devido à sobrecarga de transferência de dados e problemas de precisão em algumas situações.

Essas observações destacam que a escolha do método ideal depende do tamanho dos dados e das características do problema. Enquanto o SerialCPU pode ser suficiente para textos pequenos e simples, o ParallelCPU oferece um equilíbrio entre desempenho e confiabilidade em uma ampla gama de cenários. O ParallelGPU, por sua vez, pode ser mais adequado para aplicações envolvendo grandes volumes de dados, onde os benefícios do paralelismo massivo superam seus custos iniciais.

Os resultados reforçam a importância de compreender as vantagens e limitações de cada abordagem e alinhar sua aplicação às necessidades específicas, maximizando a eficiência computacional e a precisão das soluções desenvolvidas.
