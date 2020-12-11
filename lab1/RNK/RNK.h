#ifndef LAB1_RNK_H
#define LAB1_RNK_H

#include <iostream>

class RNK {
public:
    enum Nucleotide{
        T, C, G, A
    };
private:
    size_t *array = nullptr;
    size_t array_size = 0;
    size_t last_nucleotide = 0;

    void giveMoreMemory(size_t,size_t);
    class const_reference;
    class reference {
    private:
        RNK *rnk_pt;
        size_t arr_pos;
        size_t size_t_pos;
        size_t pos;
        void writeBits(size_t);
    public:
        int readBits() const;
        reference(RNK *,size_t,size_t,size_t);
        Nucleotide operator!() const;
        reference& operator=(Nucleotide);
        reference& operator=(const reference&);
        reference& operator=(const const_reference&);
        bool operator==(const reference&) const;
        bool operator==(const const_reference&) const;
        operator Nucleotide() const;
    };
    class const_reference {
    private:
        const RNK *rnk_pt;
        size_t arr_pos;
        size_t size_t_pos;
        size_t pos;
    public:
        int readBits() const;
        const_reference(const RNK *,size_t,size_t,size_t);
        Nucleotide operator!() const;
        bool operator==(const reference&) const;
        bool operator==(const const_reference&) const;
        operator Nucleotide() const;
    };
public:
    RNK();
    ~RNK();
    RNK(const RNK&);

    reference operator[](size_t);
    const_reference operator[](size_t) const;
    RNK& operator=(const RNK&);
    RNK& operator+=(const RNK&);
    RNK operator+(const RNK&) const;
    bool operator==(const RNK&) const;
    bool operator!=(const RNK&) const;
    RNK operator!() const;
    RNK split(size_t) const;
    bool isComplementary(const RNK&) const;
};

#endif
