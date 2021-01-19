#ifndef LAB4_CSVPARSER_H
#define LAB4_CSVPARSER_H

#include <iostream>
#include <tuple>
#include <fstream>
#include <vector>
#include <sstream>

//parse_tuple
template<typename Type>
Type parse_word(const std::string &str){
    std::stringstream ss(str);
    Type value;
    ss >> std::noskipws >> value;
    return value;
}


template<typename Type, unsigned N, unsigned Last>
class tuple_parser {
public:
    static void parse(Type &tuple, const std::vector<std::string> &words) {
        std::get<N>(tuple) = parse_word<typename std::tuple_element<N, Type>::type>(words[N]);
        tuple_parser<Type, N + 1, Last>::parse(tuple, words);
    }
};

template<typename Type, unsigned N>
class tuple_parser<Type, N, N> {
public:
    static void parse(Type &tuple, const std::vector<std::string> &words) {
        std::get<N>(tuple) = parse_word<typename std::tuple_element<N, Type>::type>(words[N]);
    }
};


//tuple printer
template<typename Type, unsigned N, unsigned Last>
class tuple_printer {
public:
    static void print(std::ostream& output, const Type& value) {
        output << std::get<N>(value) << ", ";
        tuple_printer<Type, N + 1, Last>::print(output, value);
    }
};

template<typename Type, unsigned N>
class tuple_printer<Type, N, N> {
public:
    static void print(std::ostream& output, const Type& value) {
        output << std::get<N>(value);
    }
};

template<typename... Args>
std::ostream& operator<<(std::ostream& output, const std::tuple<Args...>& value) {
    output << "(";
    tuple_printer<std::tuple<Args...>, 0, sizeof...(Args) - 1>::print(output, value);
    output << ")";
    return output;
}

template<typename... Args>
class CSVParser {
private:
    std::ifstream &file;
    int skip_lines;
    char col_sep = ',';
    char row_sep = '\n';
    int length = 0;
public:
    CSVParser(std::ifstream& _file, size_t _skip_lines): file(_file), skip_lines(_skip_lines){
        char c;
        while (file.get(c)){
            if (c == row_sep) length++;
        }
        length++;
        file.clear();
        file.seekg(0);
    }
    std::string get_row(){
        std::string new_string;
        char c;
        while (file.get(c)){
            if (c == row_sep) break;
            new_string.push_back(c);
        }
        return new_string;
    }
    std::string get_row(int idx){
        file.clear();
        file.seekg(0);
        for (int i = 0; i < idx; i++){
            get_row();
        }
        return get_row();
    }
    std::tuple<Args...> parse_row(std::string &str){
        std::vector<std::string> words;
        std::string current_word;
        for (int i = 0; i < str.size(); i++){
            if (str[i] == col_sep){
                words.push_back(current_word);
                current_word.clear();
            } else {
                current_word.push_back(str[i]);
            }
        }
        words.push_back(current_word);
        std::tuple<Args...> new_tuple;
        tuple_parser<std::tuple<Args...>, 0, sizeof...(Args) - 1>::parse(new_tuple, words);
        return new_tuple;
    }
    class CSVIterator{
    private:
        int index;
        CSVParser<Args...> *parser;
        std::string row;
    public:
        bool operator!=(const CSVIterator &iter)const{
            return parser != iter.parser || index != iter.index;
        }
        CSVIterator& operator++ (){
            if(index < parser->length){
                index++;
            }
            return *this;
        }
        std::tuple<Args...> operator*(){
            row = parser->get_row(index);
            return parser->parse_row(row);
        }
        CSVIterator(int _index, CSVParser<Args...> *_parser): index(_index), parser(_parser){}
    };
    CSVIterator begin() {return CSVIterator(skip_lines, this);}
    CSVIterator end() {return CSVIterator(length, this);}
};

#endif
