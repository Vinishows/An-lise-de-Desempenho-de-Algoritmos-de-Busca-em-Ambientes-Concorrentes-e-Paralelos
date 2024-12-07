inline int isAlphanumeric(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
}

__kernel void countWord(
    __global const char *text,
    __global const char *word,
    __global int *occurrences,
    const int textLength,
    const int wordLength) {

    int id = get_global_id(0); // ID único da thread
    int totalThreads = get_global_size(0); // Número total de threads

    int count = 0;

    // Cada thread processa uma parte do texto
    for (int i = id; i < textLength - wordLength + 1; i += totalThreads) {
        int match = 1;

        // Comparar a substring com a palavra-alvo
        for (int j = 0; j < wordLength; j++) {
            if (text[i + j] != word[j]) {
                match = 0;
                break;
            }
        }

        // Verificar delimitadores (início e fim da palavra encontrada)
        if (match) {
            int isStartValid = (i == 0 || !isAlphanumeric(text[i - 1])); // Delimitador antes da palavra
            int isEndValid = (i + wordLength >= textLength || !isAlphanumeric(text[i + wordLength])); // Delimitador após a palavra

            if (isStartValid && isEndValid) {
                count++;
            }
        }
    }

    // Armazenar a contagem individual de cada thread
    occurrences[id] = count;
}
