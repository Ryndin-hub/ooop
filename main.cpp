#include <iostream>

enum Nucleotide{
    T, C, G, A
};

void setBits(size_t *pt, size_t pos, size_t val){
    size_t shift = 8 * sizeof(size_t) - 2 - pos * 2;
    size_t mask = (size_t) 3 << (shift);
    *pt = (*pt & (~mask)) | (val << shift);
}

int readBits(size_t *pt, size_t pos){
    int shift = 8 * sizeof(size_t) - 2 - pos * 2;
    return (*pt >> shift) & 3;
}

class reference {
private:
    size_t *pt;
    size_t pos;
public:
    reference(size_t *pointer, size_t position){
        pt = pointer;
        pos = position;
    }
    reference& operator=(int nucleotide){
        setBits(pt,pos,nucleotide);
        return *this;
    }
    reference& operator=(reference& ref){
        setBits(pt,pos,readBits(ref.pt,ref.pos));
        return *this;
    }
    bool operator==(reference& ref){
        return readBits(pt,pos) == readBits(ref.pt,ref.pos);
    }
    operator int() const{
        return readBits(pt,pos);
    }
};

class RNK {
private:
    size_t *array;
    size_t array_size;
    size_t last_nucleotide;

    void giveMoreMemory() {
        size_t * new_array = new size_t[array_size * 2];
        for (int i = 0; i < array_size; i++) {
            new_array[i] = array[i];
        }
        for (int i = array_size; i < array_size * 2; i++) {
            new_array[i] = 0;
        }
        delete array;
        array = new_array;
        array_size *= 2;
    }

public:
    RNK() {
        array = new size_t[10];
        array_size = 10;
        last_nucleotide = 0;
        for (int i = 0; i < array_size; i++) {
            array[i] = 0;
        }
    }

    reference operator[](size_t pos) {
        while ((pos / (4 * sizeof(size_t))) + 1 > array_size) {
            giveMoreMemory();
        }
        if (pos > last_nucleotide) last_nucleotide = pos;
        reference ref(&array[pos / (4 * sizeof(size_t))], pos % (4 * sizeof(size_t)));
        return ref;
    }

    RNK &operator=(RNK rnk) {
        array = new size_t[rnk.array_size];
        array_size = rnk.array_size;
        last_nucleotide = rnk.last_nucleotide;
        for (int i = 0; i < array_size; i++) {
            array[i] = rnk.array[i];
        }
        return *this;
    }
    RNK& operator+=(RNK rnk){
        size_t tmp_last_nucleotide = last_nucleotide;
        for (int i = 0; i < rnk.array_size * 4 * sizeof(size_t); i++){
            this->operator[](i + tmp_last_nucleotide + 1) = rnk[i];
        }
        return *this;
    }
    RNK& operator+(RNK rnk){
        RNK *rnk_new = new RNK;
        for (int i = 0; i <= last_nucleotide; i++){
            rnk_new->operator[](i) = this->operator[](i);
        }
        for (int i = 0; i < rnk.array_size * 4 * sizeof(size_t); i++){
            rnk_new->operator[](i + last_nucleotide + 1) = rnk[i];
        }
        return *rnk_new;
    }
    bool operator==(RNK rnk){
        for (int i = 0; i < array_size; i++) {
            if (array[i] != rnk.array[i]) return false;
        }
        return true;
    }
};

int main() {

    printf("\nop+ test\n");
    RNK test1, test2, test3;
    for (int i = 0; i < 100; i+=2){
        test1[i] = A;
        test1[i+1] = C;
    }
    for (int i = 0; i < 100; i++){
        test2[i] = G;
    }
    test3 = test1 + test2;
    for (int i = 0; i < 250; i++){
        printf("%d ", test3[i] & 3);
    }


    printf("\n100mil test\n");
    RNK test4;
    for (int i = 0; i < 100000000; i++){
        test4[i] = A;
    }
    printf("%d ", test4[100000000 / 2] & 3);


    printf("\ncopy test\n");
    RNK test5;
    test5[1000] = test4[100000000 - 1000] = G;
    printf("%d ", test5[1000] & 3);

    return 0;
}
