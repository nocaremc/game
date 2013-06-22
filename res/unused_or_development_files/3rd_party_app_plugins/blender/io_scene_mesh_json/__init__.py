# ########################################
#
#  Blender Mesh Exporter
#  Exports a Mesh in Blender3D
#
#  Author: Nocare
#  Copyright: 2013
#  License: MIT
#  Use this exporter as you see fit. You may modify and distribute
#  as your see fit, including comercially.
#  Credit to me is nice, but not required
#
# ########################################

bl_info ={
    "name" : "JSON Mesh exporter",
    "author" : "Nocare",
    "blender" : (2,66,0),
    "location" : "File -> Import-Export",
    "description" : "Export Mesh in JSON format",
    "warning" : "",
    "category" : "Import-Export"
}

if "bpy" in locals():
    import imp
    if "export_mesh" in locals():
        imp.reload(export_mesh)

import bpy
from bpy.props import (BoolProperty, FloatProperty, StringProperty, EnumProperty)
from bpy_extras.io_utils import (ExportHelper, ImportHelper, path_reference_mode, axis_conversion)

class ExportMesh(bpy.types.Operator, ExportHelper):
    bl_idname = "export_mesh.json"
    bl_label = "Export Mesh"
    bl_options = {'PRESET'}

    filename_ext = ".json"

    axis_forward = EnumProperty(
        name="Forward",
        items=(('X', "X Forward", ""),
                    ('Y', "Y Forward", ""),
                    ('Z', "Z Forward", ""),
                    ('-X', "-X Forward", ""),
                    ('-Y', "-Y Forward", ""),
                    ('-Z', "-Z Forward", ""),
        ),
        default='-Z',
    )

    axis_up = EnumProperty(
        name="Up",
        items=(('X', "X Up", ""),
                    ('Y', "Y Up", ""),
                    ('Z', "Z Up", ""),
                    ('-X', "-X Up", ""),
                    ('-Y', "-Y Up", ""),
                    ('-Z', "-Z Up", ""),
        ),
        default='Y'
    )

    global_scale = FloatProperty(
        name="Scale",
        description="Scale all data",
        min=0.01, max=1000.0,
        soft_min=0.01,
        soft_max=1000.0,
        default=1.0,
    )

    path_mode = path_reference_mode
    check_extension = True

    def execute(self, context):
        from . import export_mesh
        import imp
        import sys
        # Reload module every time this function runs.
        # So that changes dont require Addon enable/disable or blender restarts
        if sys.modules['io_scene_mesh_json.export_mesh']:
            imp.reload(sys.modules['io_scene_mesh_json.export_mesh'])

        from mathutils import Matrix
        keywords = self.as_keywords(
            ignore=("axis_forward", "axis_up", "global_scale", "check_existing", "filter_glob")
        )

        global_matrix = Matrix()

        global_matrix[0][0] = \
        global_matrix[1][1] = \
        global_matrix[2][2] = self.global_scale

        global_matrix = (
            global_matrix * axis_conversion(
                to_forward=self.axis_forward, to_up=self.axis_up
            ).to_4x4()
        )

        keywords["global_matrix"] = global_matrix
        return export_mesh.save(self, context, **keywords)


def menu_func_export(self, context):
    self.layout.operator(ExportMesh.bl_idname, text="Mesh (.json)")

def register():
    bpy.utils.register_module(__name__)
    bpy.types.INFO_MT_file_export.append(menu_func_export)


def unregister():
    bpy.utils.unregister_module(__name__)
    bpy.types.INFO_MT_file_export.remove(menu_func_export)

if __name__ == "__main__":
    register()