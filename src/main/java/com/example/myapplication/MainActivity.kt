package com.example.gg2021_hw1

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.lang.Exception
import java.lang.Math.sqrt
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {
    private lateinit var glView: GLSurfaceView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glView = MyGLSurfaceView(this)

        setContentView(glView)
    }
}

class MyGLSurfaceView(context: Context): GLSurfaceView(context){
    private val renderer: MyGLRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = MyGLRenderer(context)
        setRenderer(renderer)
    }
}

class MyGLRenderer(context: Context): GLSurfaceView.Renderer{
    private val mContext: Context = context
    private var vPMatrix = FloatArray(16)
    private var projectionMatrix = FloatArray(16)
    private var viewMatrix = FloatArray(16)
    //P. model matrix & 매 프레임 변화 matrix 선언F
    private var CubemodelMatrix = FloatArray(16)
    private var HumanmodelMatrix = FloatArray(16)
    private var TeapotmodelMatrix = FloatArray(16)
    private var CubeframeMatrix = FloatArray(16)
    private var HumanframeMatrix = FloatArray(16)
    private var HumanAfterframeMatrix = FloatArray(16)
    private var TeapotframeMatrix = FloatArray(16)
    private var TeapotAfterframeMatrix = FloatArray(16)
    private var RotateAngle = Math.toRadians(0.8)

    //P. object 선언F
    private lateinit var cube: MyObject
    private lateinit var human: MyObject
    private lateinit var teapot: MyObject


    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        //P. object 초기화 F
        cube = MyObject(mContext,0)
        human = MyObject(mContext, 1)
        teapot = MyObject(mContext, 2)

        //P. model matrix & 매 프레임 변화 matrix 초기화 F
        Matrix.setIdentityM(projectionMatrix, 0)
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.setIdentityM(CubemodelMatrix, 0)
        Matrix.setIdentityM(HumanmodelMatrix, 0)
        Matrix.setIdentityM(TeapotmodelMatrix, 0)
        Matrix.setIdentityM(CubeframeMatrix, 0)
        Matrix.setIdentityM(HumanframeMatrix, 0)
        Matrix.setIdentityM(HumanAfterframeMatrix, 0)
        Matrix.setIdentityM(TeapotframeMatrix, 0)
        Matrix.setIdentityM(TeapotAfterframeMatrix, 0)
    }

    override fun onDrawFrame(p0: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        //P. 아래 구현한 mySetLookAtM function 으로 수정 F
        mySetLookAtM(viewMatrix, 0, 1.0f, 1.0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        //P. 각 object 별 매 프레임 변화 matrix 와 model matrix 를 multiply F
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(CubemodelMatrix, 0, CubemodelMatrix, 0, CubeframeMatrix, 0)
        Matrix.multiplyMM(HumanmodelMatrix, 0, HumanmodelMatrix, 0, HumanframeMatrix, 0)
        Matrix.multiplyMM(TeapotmodelMatrix, 0, TeapotmodelMatrix, 0, TeapotframeMatrix, 0)
        //P. object draw F
        cube.draw(vPMatrix)
        human.draw(vPMatrix)
        teapot.draw((vPMatrix))
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0,0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        //P.  아래 구현한 myFrustumM function 으로 수정 F
        myFrustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 2f, 10f)
    }
}

//P. vecNormalize function 구현: 벡터 정규화 함수 (mySetLookAtM function 구현 시 사용)
public fun vecNormalize(rm: FloatArray){
    val result:Float = rm[0]*rm[0] + rm[1]*rm[1] + rm[2]*rm[2] + rm[3]*rm[3] + rm[4]*rm[4] + rm[5]*rm[5] + rm[6]*rm[6] + rm[7]*rm[7] +
            rm[8]*rm[8] + rm[9]*rm[9] + rm[10]*rm[10] + rm[11]*rm[11] + rm[12]*rm[12] + rm[13]*rm[13] + rm[14]*rm[14] + rm[15]*rm[15]
    rm[0] = rm[0]/sqrt(result)
    rm[1] = rm[1]/sqrt(result)
    rm[2] = rm[2]/sqrt(result)
    rm[3] = rm[3]/sqrt(result)
    rm[4] = rm[4]/sqrt(result)
    rm[5] = rm[5]/sqrt(result)
    rm[6] = rm[6]/sqrt(result)
    rm[7] = rm[7]/sqrt(result)
    rm[8] = rm[8]/sqrt(result)
    rm[9] = rm[9]/sqrt(result)
    rm[10] = rm[10]/sqrt(result)
    rm[11] = rm[11]/sqrt(result)
    rm[12] = rm[12]/sqrt(result)
    rm[13] = rm[13]/sqrt(result)
    rm[14] = rm[14]/sqrt(result)
    rm[15] = rm[15]/sqrt(result)
}
//P. mySetLookAtM function 구현: viewMatrix 구하는 함수 (Matrix library function 중 multiplyMM 만 사용 가능) F
public fun mySetLookAtM(rm: FloatArray, rmOffset: Int, eyeX: Float, eyeY: Float, eyeZ: Float, centerX: Float, centerY: Float, centerZ: Float, upX: Float, upY: Float, upZ: Float){
    val nVector = FloatArray(4)
    nVector[0] = centerX - eyeX
    nVector[1] = centerY - eyeY
    nVector[2] = centerZ - eyeZ
    nVector[3] = 0.0f
    vecNormalize(nVector)

    val aVector = FloatArray(4)
    aVector[0] = upX * nVector[0]
    aVector[1] = upX * nVector[1]
    aVector[2] = upX * nVector[2]
    aVector[3] = 0.0f


    val v0Vector = FloatArray(4)
    v0Vector[0] = upX - aVector[0]*nVector[0]
    v0Vector[1] = upY - aVector[1]*nVector[1]
    v0Vector[2] = upZ - aVector[2]*nVector[2]
    v0Vector[3] = 0.0f
    vecNormalize(v0Vector)

    val wVector = FloatArray(4)
    wVector[0] = nVector[0] * v0Vector[0]
    wVector[1] = nVector[1] * v0Vector[1]
    wVector[2] = nVector[2] * v0Vector[2]
    wVector[3] = 0.0f

    rm[0] = wVector[0]
    rm[1] = wVector[1]
    rm[2] = wVector[2]
    rm[3] = 0.0f
    rm[4] = v0Vector[0]
    rm[5] = v0Vector[1]
    rm[6] = v0Vector[2]
    rm[7] = 0.0f
    rm[8] = -nVector[0]
    rm[9] = -nVector[1]
    rm[10] = -nVector[2]
    rm[11] = 0.0f
    rm[12] = 0.0f
    rm[13] = 0.0f
    rm[14] = 0.0f
    rm[15] = 1.0f

}
//P. myFrustumM function 구현: projectionMatrix 구하는 함수 (Matrix library function 중 multiplyMM 만 사용 가능) F
public fun myFrustumM(m: FloatArray, offset: Int, left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float){
    m[0] = 2 * near / (right - left)
    m[5] = 2 * near / (top - bottom)
    m[8] = (right + left) / (right - left)
    m[9] = (top + bottom) / (top - bottom)
    m[10] = -(far + near) / (far - near)
    m[11] = -1.0f
    m[14] = -(2 * far * near) / (far - near)
    m[15] = 0.0f
}

//PP. cube, person, teapot 모두 포함할 수 있는 Object class 로 수정
class MyObject(context: Context, objectname: Int){

    //P. 아래 shader code string 지우고, res/raw 에 위치한 vertex.glsl , fragment.glsl 로드해서 vertexShaderCode, fragmentShaderCode 에 넣기
    private val vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}"

    private val fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}"

    //P. model matrix handle 변수 추가 선언F
    private var vPMatrixHandle: Int = 0
    private var CubemodelMatrixHandle: Int = 0
    private var HumanmodelMatrixHandle: Int = 0
    private var TeapotmodelMatrixHandle: Int = 0

    val color = floatArrayOf(1.0f, 0.980392f, 0.980392f, 0.3f)

    private var mProgram: Int

    private var vertices = mutableListOf<Float>()
    private var faces = mutableListOf<Short>()
    private lateinit var verticesBuffer: FloatBuffer
    private lateinit var facesBuffer: ShortBuffer

    init {
        try {
            val scanner = Scanner(context.assets.open("cube.obj"))
            while (scanner.hasNextLine()){
                val line = scanner.nextLine()
                if (line.startsWith("v  ")){
                    val vertex = line.split(" ")
                    val x = vertex[2].toFloat()
                    val y = vertex[3].toFloat()
                    val z = vertex[4].toFloat()
                    vertices.add(x)
                    vertices.add(y)
                    vertices.add(z)
                }
                else if (line.startsWith("f ")) {
                    val face = line.split(" ")
                    val vertex1 = face[1].split("/")[0].toShort()
                    val vertex2 = face[2].split("/")[0].toShort()
                    val vertex3 = face[3].split("/")[0].toShort()
                    faces.add(vertex1)
                    faces.add(vertex2)
                    faces.add(vertex3)
                }
            }

            verticesBuffer = ByteBuffer.allocateDirect(vertices.size * 4).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    for (vertex in vertices){
                        put(vertex)
                    }
                    position(0)
                }
            }

            facesBuffer = ByteBuffer.allocateDirect(faces.size * 2).run {
                order(ByteOrder.nativeOrder())
                asShortBuffer().apply {
                    for (face in faces){
                        put((face-1).toShort())
                    }
                    position(0)
                }
            }
        } catch (e: Exception){
            Log.e("file_read", e.message.toString())
        }

        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    val COORDS_PER_VERTEX = 3

    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0

    private val vertexStride: Int = COORDS_PER_VERTEX * 4

    //PP. cube, person, teapot 의 world transform 및 매 프레임 변화를 반영할 수 있는 draw function 으로 수정
    fun draw(mvpMatrix: FloatArray){
        GLES20.glUseProgram(mProgram)

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(
                    it,
                    COORDS_PER_VERTEX,
                    GLES20.GL_FLOAT,
                    false,
                    vertexStride,
                    verticesBuffer
            )

            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->
                GLES20.glUniform4fv(colorHandle, 1, color, 0)
            }

            vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
            GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, faces.size, GLES20.GL_UNSIGNED_SHORT, facesBuffer)

            GLES20.glDisableVertexAttribArray(it)
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}