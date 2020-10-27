#ifndef LAB1_RNK_H
#define LAB1_RNK_H

#include <iostream>

class RNK {
public:
    enum Nucleotide{
        T, C, G, A
    };
private:
    size_t *array;
    size_t array_size;
    size_t last_nucleotide;

    class reference {
    private:
        size_t *pt;
        size_t pos;

        void writeBits(size_t value){
            size_t shift = 8 * sizeof(size_t) - 2 - pos * 2;
            size_t mask = (size_t) 3 << (shift);
            *pt = (*pt & (~mask)) | (value << shift);
        }
        int readBits() const{
            size_t shift = 8 * sizeof(size_t) - 2 - pos * 2;
            return ((*pt >> shift) & 3);
        }
    public:
        reference(size_t *pointer, size_t position){
            pt = pointer;
            pos = position;
        }
        Nucleotide operator!() const{
            return (Nucleotide) (3 - readBits());
        }
        reference& operator=(Nucleotide nucleotide){
            writeBits(nucleotide);
            return *this;
        }
        reference& operator=(reference& ref){
            writeBits(ref.readBits());
            return *this;
        }
        bool operator==(const reference& ref) const{
            return readBits() == ref.readBits();
        }
        operator Nucleotide() const{
            return (Nucleotide) readBits();
        }
    };

    void giveMoreMemory(){
        size_t *new_array = new size_t[array_size * 2];
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
    reference operator()(size_t pos) const{ //как квадратные скобки, но не может выделять память
        reference ref(&array[pos / (4 * sizeof(size_t))], pos % (4 * sizeof(size_t)));
        return ref;
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
    RNK& operator=(const RNK rnk) {
        array = new size_t[rnk.array_size];
        array_size = rnk.array_size;
        last_nucleotide = rnk.last_nucleotide;
        for (int i = 0; i < array_size; i++) {
            array[i] = rnk.array[i];
        }
        return *this;
    }
    RNK& operator+=(const RNK rnk){
        size_t tmp_last_nucleotide = last_nucleotide;
        for (int i = 0; i < rnk.array_size * 4 * sizeof(size_t); i++){
            this->operator[](i + tmp_last_nucleotide + 1) = rnk(i);
        }
        return *this;
    }
    RNK& operator+(const RNK rnk) const{
        RNK *rnk_new = new RNK;
        for (int i = 0; i <= last_nucleotide; i++){
            rnk_new->operator[](i) = this->operator()(i);
        }
        for (int i = 0; i <= rnk.last_nucleotide; i++){
            rnk_new->operator[](i + last_nucleotide + 1) = rnk(i);
        }
        return *rnk_new;
    }
    bool operator==(const RNK rnk) const{
        for (int i = 0; i < array_size; i++) {
            if (array[i] != rnk.array[i]) return false;
        }
        return true;
    }
    bool operator!=(const RNK rnk) const{
        return !(*this == rnk);
    }
    RNK& operator!() const{
        RNK *rnk_new = new RNK;
        for (int i = 0; i <= last_nucleotide; i++){
            rnk_new->operator[](i) = !this->operator()(i);
        }
        return *rnk_new;
    }
    RNK& split(size_t pos) const{
        RNK *rnk_new = new RNK;
        for (int i = 0; i <= last_nucleotide - pos; i++){
            rnk_new->operator[](i) = this->operator()(i + pos);
        }
        return *rnk_new;
    }
    bool isComplementary(const RNK rnk) const{
        if (last_nucleotide != rnk.last_nucleotide) return false;
        for (int i = 0; i <= last_nucleotide; i++) {
            if (this->operator()(i) != !rnk(i)) return false;
        }
        return true;
    }
};

#endif
