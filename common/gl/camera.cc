#include "gl/camera.h"

namespace oc {

    void GLCamera::DecomposeMatrix(const glm::mat4& matrix, glm::vec3* translation,
                                   glm::quat* rotation, glm::vec3* scale) {
        translation->x = matrix[3][0];
        translation->y = matrix[3][1];
        translation->z = matrix[3][2];
        *rotation = glm::quat_cast(matrix);
        scale->x = glm::length(glm::vec3(matrix[0][0], matrix[1][0], matrix[2][0]));
        scale->y = glm::length(glm::vec3(matrix[0][1], matrix[1][1], matrix[2][1]));
        scale->z = glm::length(glm::vec3(matrix[0][2], matrix[1][2], matrix[2][2]));
        if (glm::determinant(matrix) < 0.0)
            scale->x = -scale->x;
    }

    float GLCamera::Diff(glm::vec3& pa, glm::vec3& pb, glm::quat &a, glm::quat &b)
    {
        if (glm::abs(b.x) < 0.005)
            if (glm::abs(b.y) < 0.005)
                if (glm::abs(b.z) < 0.005)
                    if (glm::abs(b.w) - 1 < 0.005)
                        return 1;
        glm::vec3 diff = glm::eulerAngles(glm::inverse(a) * b);
        diff = glm::abs(diff);
        if (diff.x > M_PI)
            diff.x -= M_PI;
        if (diff.y > M_PI)
            diff.y -= M_PI;
        if (diff.z > M_PI)
            diff.z -= M_PI;
        float pos = glm::length(pa - pb) * 100.0f;
        float rot = glm::degrees(glm::max(glm::max(diff.x, diff.y), diff.z));
        return glm::max(pos, rot);
    }

    glm::mat4 GLCamera::GetTransformation() const {
        glm::mat4 transform = glm::scale(glm::mat4_cast(rotation), scale);
        transform[3][0] = position.x;
        transform[3][1] = position.y;
        transform[3][2] = position.z;
        return transform;
    }

    glm::mat4 GLCamera::GetView() const {
        return glm::inverse(GetTransformation());
    }

    void GLCamera::SetTransformation(const glm::mat4& transform) {
        DecomposeMatrix(transform, &position, &rotation, &scale);
    }
}
