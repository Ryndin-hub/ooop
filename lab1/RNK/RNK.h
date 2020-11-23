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

    void giveMoreMemory(size_t arr_pos, size_t pos);

    class reference {
    private:
        RNK *rnk_pt;
        size_t arr_pos;
        size_t size_t_pos;
        size_t pos;
        void writeBits(size_t value);
        int readBits() const;
    public:
        reference(const RNK *pointer, size_t array_position, size_t size_t_position, size_t position);
        Nucleotide operator!() const;
        reference& operator=(Nucleotide nucleotide);
        reference& operator=(const reference& ref);
        bool operator==(const reference& ref) const;
        operator Nucleotide() const;
    };
public:
    RNK();
    ~RNK();

    reference operator[](size_t pos) const;
    RNK& operator=(const RNK& rnk);
    RNK& operator+=(const RNK& rnk);
    RNK operator+(const RNK& rnk) const;
    bool operator==(const RNK& rnk) const;
    bool operator!=(const RNK& rnk) const;
    RNK operator!() const;
    RNK split(size_t pos) const;
    bool isComplementary(const RNK& rnk) const;
};

#endif
