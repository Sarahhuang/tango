#ifndef DATA_FILE3D_H
#define DATA_FILE3D_H

#include <map>
#include <string>
#include "data/mesh.h"

namespace oc {
    enum TYPE{OBJ, PLY};

class File3d {
public:
    File3d(std::string filename, bool writeAccess);
    ~File3d();
    void ReadModel(int subdivision, std::vector<oc::Mesh>& output);
    void WriteModel(std::vector<Mesh>& model);

private:
    void CleanStr(std::string& str);
    glm::ivec3 DecodeColor(unsigned int c);
    void ParseOBJ(int subdivision, std::vector<oc::Mesh> &output);
    void ParsePLY(int subdivision, std::vector<Mesh> &output);
    void ReadHeader();
    unsigned int ScanDec(char *line, int offset);
    bool StartsWith(std::string s, std::string e);
    void WriteHeader(std::vector<Mesh>& model);
    void WritePointCloud(Mesh& mesh);
    void WriteFaces(Mesh& mesh, int offset);

    TYPE type;
    std::string path;
    bool writeMode;
    unsigned int vertexCount;
    FILE* file;
    std::map<std::string, int> fileToIndex;
    std::map<std::string, glm::vec3> keyToColor;
    std::map<std::string, std::string> keyToFile;
};
}

#endif
