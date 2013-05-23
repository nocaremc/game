# ########################################
#
#  Blender Bones Exporter
#  Exports an Armeture in blender
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
"""
float AngleBetween(Vector3 from, Vector3 dest)  {
    float len = from.magnitude *  dest.magnitude;
    if(len < Mathf.Epsilon) len = Mathf.Epsilon;

    float f = Vector3.Dot(from,dest) / len;
    if(f>1.0f)f=1.0f;
    else if ( f < -1.0f) f = -1.0f;

    return Mathf.Acos(f) * 180.0f / (float)Math.PI;
}"""

def angleBetween(orig, dest):
    len = orig.magnitude * dest.magnitude
    if len < sys.float_info.epsilon:
        len = sys.float_info.epsilon

    f = mathutils.Vector.dot(orig, dest) / len

    if f > 1.0:
        f = 1.0
    elif f < -1.0:
        f = -1.0

    return math.acos(f) * 180.0 / math.pi


"""     Returns text(space) equivelent of I tabs     """
def tab(i=0):
    s=""
    for x in range(0, i):
        s = s +"    "
    
    return s

    
def vectorToJSONList(name, vector, isEnd=False):
    global tabs
    t = tab(tabs)
    returnStr="%s\"%s\":\n%s{\n" % (t, name,t)
    tabs+=1
    t = tab(tabs)
    count = 0
    var = ["x", "y", "z"]
    
    for v in vector:
        if count != 2:
            returnStr += ("%s\"%s\": %s,\n" % (t,var[count], v))
        else:
            returnStr += ("%s\"%s\": %s\n" % (t,var[count], v))
        count+=1
    
    tabs-=1
    t = tab(tabs)
    returnStr += ("%s}" % t)
    if isEnd == False:
        returnStr += ",\n"
    else:
        returnStr += "\n"
    #tabs -=1
    #t = tab(tabs)
    #returnStr += (t+"],\n")
    return returnStr
    
"""     Writes bone data to file, splitting away IK's     """
def writeBoneData(fw, objectMatrix, bone, isEnd=False):
    global ikList
    global tabs
    # We'll be checking for bones with "ik" in the name. e.g: name.ik.side
    splitName = bone.name.split(".")
    # If the name has ik, add it to the list of ik's and exit this function
    for s in splitName:
        if s == "ik":
            ikList.append(bone)
            return
    
    tabs=2
    t = tab(tabs) # Set tab index to 1
    #fw("%s\"%s\":\n%s{\n" % (t,bone.name,t))
    fw("%s{\n" % t)
    
    #fw(t+"{\n")
    
    tabs+=1
    t = tab(tabs)
    fw("%s\"name\": \"%s\",\n" % (t, bone.name))
    if bone.parent != None:
        fw("%s\"parent\": \"%s\",\n" % (t, bone.parent.name))
        fw("%s\"connected\": \"%s\",\n" % (t, bone.use_connect))
        fw("%s\"inherit_rotate\": \"%s\",\n" % (t, bone.use_inherit_rotation))
        fw("%s\"inherit_scale\": \"%s\",\n" % (t, bone.use_inherit_scale))
        angle = angleBetween(bone.parent.tail_local, bone.vector)
        fw("%s\"angle\": %s,\n" % (t, angle))
    else:
        fw("%s\"parent\": %s,\n" % (t, "null"))
        fw("%s\"connected\": \"%s\",\n" % (t, False))
        fw("%s\"inherit_rotate\": \"%s\",\n" % (t, False))
        fw("%s\"inherit_scale\": \"%s\",\n" % (t, False))
        fw("%s\"angle\": %s,\n" % (t, 0.00))
    
    head_pos = objectMatrix * bone.head_local
    tail_pos = objectMatrix * bone.tail_local
    
    fw("%s\"length\": %s,\n" % (t, bone.length))
    fw("%s\"xPos\": %s,\n" % (t, head_pos[0]))
    fw("%s\"yPos\": %s,\n" % (t, head_pos[1]))
    fw("%s\"zPos\": %s,\n" % (t, head_pos[2]))
    #fw("%s\"posMatrix\": %s,\n" % (t, head_pos))
   # Print whether or not this bone deforms mesh. Convert python boolean to string, then lowercase
   # Javascript booleans are lowercase
    fw("%s\"deforms\": %s,\n" % (t, (str(bone.use_deform)).lower()))
    #fw(vectorToJSONList("tail",bone.tail))
    #fw(vectorToJSONList("tail_local",bone.tail_local))
    #fw(vectorToJSONList("head",bone.head))
    #fw(vectorToJSONList("head_local",bone.head_local))
    fw(vectorToJSONList("vector",tail_pos, True))
    tabs-=1
    t = tab(tabs)
    fw("%s}" % t)
    if isEnd == False:
        fw(",\n")
    else:
        fw("\n")
    #fw(t+"matrix : %s\n" % bone.matrix)


def write_file(context, filepath, objects, scene,
        EXPORT_GLOBAL_MATRIX=None,
        EXPORT_PATH_MODE='AUTO',
        ):
    global tabs
    
    # Only supporting one armeture object, so clamp
    object = objects[0]
    # Get position of object relative to world
    positionTrans = object.matrix_world + EXPORT_GLOBAL_MATRIX
    position  = object.location * positionTrans
    
    
    # Skelaton shouldnt be parented to anything, but the mesh should be to the skelaton.
    # Its a hack to prevent errors if its not an armeture being exported
    if object.parent != None:
        return;
    
    if EXPORT_GLOBAL_MATRIX is None:
        EXPORT_GLOBAL_MATRIX = mathutils.Matrix()

    # Create file at designated path
    file = open(filepath, "w", encoding="utf8", newline="\n")
    # Create shortcut function referance to file.write
    fw = file.write
    fw("{\n")
    tabs+=1
    t = tab(tabs)
    fw("%s\"blender_version\": \"%s\",\n" % (t, bpy.app.version_string))
    fw("%s\"blender_site\": \"http://www.blender.org\",\n" % t)
    fw("%s\"project_file\": \"%s\",\n" % (t, os.path.basename(bpy.data.filepath)))
    fw("%s\"name\": \"%s\",\n" % (t, object.name))
    fw("%s\"xPos\": \"%s\",\n" % (t, position[0]))
    fw("%s\"yPos\": \"%s\",\n" % (t, position[1]))
    fw("%s\"zPos\": \"%s\",\n" % (t, position[2]))
    fw("%s\"boneList\":\n%s[\n" % (t,t))

    rng = len(object.data.bones)
    matrix = EXPORT_GLOBAL_MATRIX#object.matrix_world + EXPORT_GLOBAL_MATRIX
    
    for x in range(rng):
        if x+2 > rng:
            writeBoneData(fw,matrix,object.data.bones[x], True)
        else:
            writeBoneData(fw,matrix,object.data.bones[x])
    
    tabs-=1
    t = tab(tabs)
    #fw(t+"]\n")
    fw("%s],\n" % t) # End bone list
    fw("%s\"file_export_time\": \"%0.4f sec\"\n" % (t, (time.time() - time_start)))
    fw("}")
    #fw("\n#Finished: %.4f sec" % (time.time() - time_start))
    file.close()

def save(operator, context, filepath="",
         use_selection=True,
         global_matrix=None,
         path_mode='AUTO'
        ):

    import time
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