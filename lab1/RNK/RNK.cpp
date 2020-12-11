#include "RNK.h"

void RNK::giveMoreMemory(size_t arr_pos, size_t pos){
    while (arr_pos + 1 > array_size){
        auto *new_array = new size_t[array_size * 2];
        for (int i = 0; i < array_size; i++) {
            new_array[i] = array[i];
        }
        for (int i = array_size; i < array_size * 2; i++) {
            new_array[i] = 0;
        }
        delete[] array;
        array = new_array;
        array_size *= 2;
    }
    if (pos > last_nucleotide) last_nucleotide = pos;
}

void RNK::reference::writeBits(size_t value){
    rnk_pt->giveMoreMemory(arr_pos,pos);
    size_t shift = 8 * sizeof(size_t) - 2 - size_t_pos * 2;
    size_t mask = (size_t) 3 << (shift);
    rnk_pt->array[arr_pos] = (rnk_pt->array[arr_pos] & (~mask)) | (value << shift);
}

int RNK::reference::readBits() const{
    if (pos > rnk_pt->last_nucleotide) return static_cast<Nucleotide>(NULL);
    size_t shift = 8 * sizeof(size_t) - 2 - size_t_pos * 2;
    return (int)(((rnk_pt->array[arr_pos]) >> shift) & 3u);
}

RNK::reference::reference(RNK *pointer, size_t array_position, size_t size_t_position, size_t position) : rnk_pt(nullptr) {
    rnk_pt = pointer;
    arr_pos = array_position;
    size_t_pos = size_t_position;
    pos = position;
}

RNK::Nucleotide RNK::reference::operator!() const{
    return (Nucleotide) (3 - readBits());
}

RNK::reference& RNK::reference::operator=(Nucleotide nucleotide){
    writeBits(nucleotide);
    return *this;
}

RNK::reference& RNK::reference::operator=(const RNK::reference& ref){
    if (this == &ref) return *this;
    writeBits(ref.readBits());
    return *this;
}

RNK::reference& RNK::reference::operator=(const RNK::const_reference& ref){
    writeBits(ref.readBits());
    return *this;
}

bool RNK::reference::operator==(const RNK::reference& ref) const{
    return readBits() == ref.readBits();
}

bool RNK::reference::operator==(const RNK::const_reference& ref) const{
    return readBits() == ref.readBits();
}

RNK::reference::operator Nucleotide() const{
    return (Nucleotide) readBits();
}

int RNK::const_reference::readBits() const{
    if (pos > rnk_pt->last_nucleotide) return 0;
    size_t shift = 8 * sizeof(size_t) - 2 - size_t_pos * 2;
    return (int)(((rnk_pt->array[arr_pos]) >> shift) & 3u);
}

RNK::const_reference::const_reference(const RNK *pointer, size_t array_position, size_t size_t_position, size_t position) : rnk_pt(nullptr) {
    rnk_pt = pointer;
    arr_pos = array_position;
    size_t_pos = size_t_position;
    pos = position;
}

RNK::Nucleotide RNK::const_reference::operator!() const{
    return (Nucleotide) (3 - readBits());
}

bool RNK::const_reference::operator==(const RNK::reference& ref) const{
    return readBits() == ref.readBits();
}

bool RNK::const_reference::operator==(const RNK::const_reference& ref) const{
    return readBits() == ref.readBits();
}

RNK::const_reference::operator Nucleotide() const{
    return (Nucleotide) readBits();
}

RNK::RNK() {
    array = new size_t[10];
    array_size = 10;
    last_nucleotide = 0;
    for (int i = 0; i < array_size; i++) {
        array[i] = 0;
    }
}

RNK::~RNK() {
    delete[] array;
}

RNK::RNK(const RNK& rnk) {
    if (this == &rnk) return;
    delete[] array;
    array = new size_t[rnk.array_size];
    array_size = rnk.array_size;
    last_nucleotide = rnk.last_nucleotide;
    for (int i = 0; i < array_size; i++) {
        array[i] = rnk.array[i];
    }
}

RNK::reference RNK::operator[](size_t pos){
    reference ref(this,pos / (4 * sizeof(size_t)), pos % (4 * sizeof(size_t)),pos);
    return ref;
}

RNK::const_reference RNK::operator[](size_t pos) const{
    const_reference ref(this,pos / (4 * sizeof(size_t)), pos % (4 * sizeof(size_t)),pos);
    return ref;
}

RNK& RNK::operator=(const RNK& rnk) {
    if (this == &rnk) return *this;
    delete[] array;
    array = new size_t[rnk.array_size];
    array_size = rnk.array_size;
    last_nucleotide = rnk.last_nucleotide;
    for (int i = 0; i < array_size; i++) {
        array[i] = rnk.array[i];
    }
    return *this;
}

RNK& RNK::operator+=(const RNK& rnk){
    size_t tmp_last_nucleotide = last_nucleotide;
    for (int i = 0; i < rnk.array_size * 4 * sizeof(size_t); i++){
        this->operator[](i + tmp_last_nucleotide + 1) = rnk[i];
    }
    return *this;
}

RNK RNK::operator+(const RNK& rnk) const{
    RNK rnk_new;
    for (int i = 0; i <= last_nucleotide; i++){
        rnk_new[i] = this->operator[](i);
    }
    for (int i = 0; i <= rnk.last_nucleotide; i++){
        rnk_new[i + last_nucleotide + 1] = rnk[i];
    }
    return rnk_new;
}

bool RNK::operator==(const RNK& rnk) const{
    for (int i = 0; i < array_size; i++) {
        if (array[i] != rnk.array[i]) return false;
    }
    return true;
}

bool RNK::operator!=(const RNK& rnk) const{
    return !(*this == rnk);
}

RNK RNK::operator!() const{
    RNK rnk_new;
    for (int i = 0; i <= last_nucleotide; i++){
        rnk_new[i] = !this->operator[](i);
    }
    return rnk_new;
}

RNK RNK::split(size_t pos) const{
    RNK rnk_new;
    if (pos >= last_nucleotide){
        for (int i = 0; i <= last_nucleotide; i++){
            rnk_new[i] = this->operator[](i + pos);
        }
    } else {
        for (int i = 0; i <= last_nucleotide - pos; i++) {
            rnk_new[i] = this->operator[](i + pos);
        }
    }
    return rnk_new;
}

bool RNK::isComplementary(const RNK& rnk) const{
    if (last_nucleotide != rnk.last_nucleotide) return false;
    for (int i = 0; i <= last_nucleotide; i++) {
        if (this->operator[](i) != !rnk[i]) return false;
    }
    return true;
}
