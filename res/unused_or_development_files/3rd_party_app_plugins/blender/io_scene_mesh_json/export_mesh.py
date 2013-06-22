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

import os
import time

import bpy
import mathutils
import bpy_extras.io_utils
import sys
import math

ikList = []
tabs = 0
time_start = 0

def mesh_triangulate(me):
    import bmesh
    bm = bmesh.new()
    bm.from_mesh(me)
    bmesh.ops.triangulate(bm, faces=bm.faces)
    bm.to_mesh(me)
    bm.free()

    
def writeMaterial(scene, filepath, path_mode, copy_set, mtl_dict):
    from mathutils import Color
    
    world = scene.world
    if world:
        world_amb = world.ambient_color
    else:
        world_amb = Color((0.0, 0.0, 0.0))

    source_dir = os.path.dirname(bpy.data.filepath)
    dest_dir = os.path.dirname(filepath)

    file = open(filepath, "w", encoding="utf8", newline="\n")
    fw = file.write
    
    
    
#def writeMeshData(fw, objectMatrix, mesh, isEnd=False):

def write_file(context, filepath, objects, scene, EXPORT_GLOBAL_MATRIX=None, EXPORT_PATH_MODE='AUTO'):
    # Only supporting one object, so clamp
    object = objects[0]
    # Get position of object relative to world
    positionTrans = object.matrix_world + EXPORT_GLOBAL_MATRIX
    position  = object.location * positionTrans
    
    if EXPORT_GLOBAL_MATRIX is None:
        EXPORT_GLOBAL_MATRIX = mathutils.Matrix()

    # Create file at designated path
    file = open(filepath, "w", encoding="utf8", newline="\n")
    # Create shortcut function referance to file.write
    fw = file.write
    fw("{\n")
    fw("\t\"blender_version\": \"%s\",\n" % bpy.app.version_string)
    fw("\t\"blender_site\": \"http://www.blender.org\",\n")
    fw("\t\"project_file\": \"%s\",\n" % os.path.basename(bpy.data.filepath))
    fw("\t\"name\": \"%s\",\n" % object.name)
    
    #rng = len(object.data.bones)
    #matrix = EXPORT_GLOBAL_MATRIX#object.matrix_world + EXPORT_GLOBAL_MATRIX
    
    #for x in range(rng):
        #if x+2 > rng:
            #writeBoneData(fw,matrix,object.data.bones[x], True)
        #else:
            #writeBoneData(fw,matrix,object.data.bones[x])
    
    fw("\t],\n") # End bone list
    fw("\t\"file_export_time\": \"%0.4f sec\"\n" % (time.time() - time_start))
    fw("}")
    file.close()

def save(operator, context, filepath="", use_selection=True, global_matrix=None, path_mode='AUTO'):
    #import time
    global time_start
    time_start = time.time()
    base_name, ext = os.path.splitext(filepath)
    context_name = [base_name, '', '', ext]  # Base name, scene name, frame number, extension

    scene = context.scene

    # Exit edit mode before exporting, so current object states are exported properly.
    if bpy.ops.object.mode_set.poll():
        bpy.ops.object.mode_set(mode='OBJECT')

    orig_frame = scene.frame_current
    
    scene_frames = [orig_frame]  # Dont export an animation.
        # Loop through all frames in the scene and export.
    for frame in scene_frames:
        scene.frame_set(frame, 0.0)
        if use_selection:
            objects = context.selected_objects
        else:
            objects = scene.objects

        full_path = ''.join(context_name)

        # erm... bit of a problem here, this can overwrite files when exporting frames. not too bad.
        # EXPORT THE FILE.
        write_file(context, full_path, objects, scene,
            EXPORT_GLOBAL_MATRIX=global_matrix,
            EXPORT_PATH_MODE=path_mode,
            )

    scene.frame_set(orig_frame, 0.0)
    return {'FINISHED'}