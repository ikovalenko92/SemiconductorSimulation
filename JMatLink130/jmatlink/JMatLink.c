/*****************************************************************************
*                           JMatLink                                         *
******************************************************************************/

// header file for java native interfaces
#include <jni.h>

// header files with stubs for native functions
#include "JMatLink.h"

// engine functions of MATLAB
#include "engine.h"

#define  BUFFERMAX 10

    long  bufferEp[BUFFERMAX];    // array to hold engine pointers
    char *bufferArray[BUFFERMAX]; // array to hold the buffers
	long  p1 = 0;

    jboolean debugB = JNI_FALSE;       // No debug messages from start


/**
 * void setDebugNATIVE( boolean d )
 * @param  d true/false to set and unset debugging messages
 */
JNIEXPORT void JNICALL Java_jmatlink_CoreJMatLink_setDebugNATIVE
            (JNIEnv *env, jobject obj, jboolean d)
{
    if (d) debugB = JNI_TRUE;
    else   debugB = JNI_FALSE;
     
}


/**
 * long  engOpenNATIVE( startcmd )    
 * @param   startCmdS start command for MATLAB
 * @return  pointer to engine instance of MATLAB
 */
JNIEXPORT jlong JNICALL Java_jmatlink_CoreJMatLink_engOpenNATIVE  
                                 (JNIEnv *env, jobject obj, jstring startCmdS_JNI)
{
    const char *openS;      // string containing the initial start command for MATLAB
	long        enginePtr;  // pointer to instance of MATLAB engine

    openS = (*env)->GetStringUTFChars(env, startCmdS_JNI, 0);

    // open instance of MATLAB's engine and check for valid pointer
    if (!(enginePtr = (long)engOpen(openS)) )
    {
        if (debugB) fprintf(stderr, "\nCan't start MATLAB engine\n");
        (*env)->ReleaseStringUTFChars(env, startCmdS_JNI, openS); // free memory
        return 0;
    }

	// free memory
    (*env)->ReleaseStringUTFChars(env, startCmdS_JNI, openS); 

    return enginePtr;
}

/**
 * jlong engOpenSingleUseNATIVE( startcmd )    
 * @param   startCmdS start command for MATLAB
 * @return  pointer to engine instance of MATLAB
 */
JNIEXPORT jlong JNICALL Java_jmatlink_CoreJMatLink_engOpenSingleUseNATIVE 
                               (JNIEnv *env, jobject obj, jstring startCmdS_JNI)
{
    const char *openS     = (*env)->GetStringUTFChars(env, startCmdS_JNI, 0);
    int         retStatus = 0;
	long        enginePtr;


    if (!(enginePtr = engOpenSingleUse(openS, NULL, &retStatus)))
    {
        if (debugB) fprintf(stderr, "\nCan't start MATLAB engine\n");
        (*env)->ReleaseStringUTFChars(env, startCmdS_JNI, openS); // free memory

        return 0;
    }

    (*env)->ReleaseStringUTFChars(env, startCmdS_JNI, openS); // free memory

	if (p1==0)
		p1=enginePtr;

    return enginePtr;
}


/**  
 * int engCloseNATIVE( long epI )
 * @param  enginePtr pointer to an instance of MATLAB's engine
 * @return status of closing the instance
 */
JNIEXPORT jint JNICALL Java_jmatlink_CoreJMatLink_engCloseNATIVE    
                                           (JNIEnv *env, jobject obj, jlong enginePtr)
{
    int retValI = engClose((Engine *)enginePtr);

    if (debugB) printf("\n engClose \n");

    return retValI;
}

/**
 *  engSetVisibleNATIVE
 * @param  epI
 * @param  evalS
 * @return 
 */
JNIEXPORT jint JNICALL Java_jmatlink_CoreJMatLink_engSetVisibleNATIVE
    (JNIEnv *env, jobject obj, jlong enginePtr, jboolean  vis)
{
    int retValI = engSetVisible((Engine *)enginePtr, vis);
    
    return retValI;
}

/**
 * engGetVisibleNATIVE
 * @param  epI
 * @param  evalS
 * @return 
 */
JNIEXPORT jint JNICALL Java_jmatlink_CoreJMatLink_engGetVisibleNATIVE
    (JNIEnv *env, jobject obj, jlong enginePtr)
{
	int vis=0;
	
	// engGetVisible only changes BIT0 of vis       
    int retValI = engGetVisible((Engine *)enginePtr, &vis);

	// check if error occured
	if (retValI == 1)
	    return 3;
	        
	return vis;
}

/**
 * int engEvalStringNATIVE( long epI, String evalS )
 * @param  epI
 * @param  evalS
 * @return 
 */
JNIEXPORT jint JNICALL Java_jmatlink_CoreJMatLink_engEvalStringNATIVE   
                        (JNIEnv *env, jobject obj, jlong enginePtr, jstring evalS_JNI)
{
    int  retValI = 0;
    const char *evalS = (*env)->GetStringUTFChars(env, evalS_JNI, 0);
 
	// evaluate expression in MATLAB
	retValI = engEvalString((Engine *)enginePtr, evalS);

	if (retValI != 0)
	   printf("engEvalStringNATIVE: return value !=0, some error\n");

    //printf("evalString %i",OpenB);

    (*env)->ReleaseStringUTFChars(env, evalS_JNI, evalS); // free memory

    return retValI;
}


/**
 * int engOutputBufferNATIVE(long enginePtr, int buflen);
 * @param  epI
 * @param  evalS
 * @return 
 */
JNIEXPORT jint JNICALL Java_jmatlink_CoreJMatLink_engOutputBufferNATIVE  
                             (JNIEnv *env, jobject obj, jlong enginePtr, jint buflen)
{
	int bufferNo = -1;
	int j;
	int retVal;
	
	// check if engine pointer is already set up for buffer
    for (j=0; j<BUFFERMAX; j++) 
    {
        if ( bufferEp[j] == enginePtr )
        {
            // buffer already created for engine pointer
            printf("used buffer %i \n", j);
			bufferNo = j;
			
			// check for removing buffer
			if (buflen == 0)
			{
        		printf("removed buffer %i \n", bufferNo);
				mxFree(bufferArray[bufferNo]);
				bufferArray[bufferNo] = NULL;
				bufferEp[bufferNo]    = NULL; 
				return 0;
			}
			
			// buffer in use, tried to create buffer twice -> error
	    	printf("tried to create buffer twice \n");
    		return 0; 
			
        }
    } // end for
    
    
    // check if someone tries to remove a not created buffer
    if ((buflen==0) && (bufferNo==-1)) {
    	//printf("tried to close not created buffer \n");
		return 0;
	}
    
    // find unused buffer
    for (j=0; j<BUFFERMAX; j++) 
    {
        if ( bufferEp[j] == NULL )
        {
            // found unused buffer space
            printf("getUnusedBuffer %i \n", j);
			bufferEp[j] = enginePtr;
			bufferNo = j;
			bufferArray[bufferNo] = mxCalloc(buflen+1, sizeof(char));
	
			retVal = engOutputBuffer((Engine *)enginePtr, bufferArray[bufferNo], buflen);

    		printf("buffer created %i \n", bufferNo);
      	    return retVal;
        }
    }

    if (debugB) printf("engOpen: getUnusedEnginePointer no more pointers available");

    printf("engOutputBuffer: did not find free buffer \n");
   
	return 0;
}

/**
 * String engGetOutputBufferNATIVE(long  enginePtr );
 * @param  epI
 * @param  evalS
 * @return 
 */
JNIEXPORT jstring JNICALL Java_jmatlink_CoreJMatLink_engGetOutputBufferNATIVE  
                             (JNIEnv *env, jobject obj, jlong enginePtr)
{
	int j;	

	// find matching buffer to engine pointer
    for (j=0; j<BUFFERMAX; j++) 
    {
        if ( bufferEp[j] == enginePtr )
        {
            // engine pointer matches a stored pointer
            printf("used buffer %i \n", j);
			return (*env)->NewStringUTF(env, bufferArray[j]);
        }
    }

	return NULL;  // not matching buffer
}

/**
 * engPutVariableNATIVE(long epI, String nameS, double[][] array );
 * @param  
 * @param  
 * @return 
 */
JNIEXPORT void JNICALL Java_jmatlink_CoreJMatLink_engPutVariableNATIVE
   (JNIEnv *env, jobject obj, jlong enginePtr, jstring arrayS_JNI, jobjectArray valueDD_JNI)
{

    int         i;
    int         j;
    mxArray    *T      = NULL;

    const char *arrayS;    
    int         rowCount;
    jobject     colPtr;
    int         colCount;
    double     *tPtrR;  
    jdouble    *arrayElements;

    arrayS       = (*env)->GetStringUTFChars(env, arrayS_JNI, 0);
    rowCount     = (*env)->GetArrayLength(env, valueDD_JNI);
    colPtr       = (*env)->GetObjectArrayElement(env, valueDD_JNI, 0);
    colCount     = (*env)->GetArrayLength(env, colPtr);

    if (debugB) printf("engPutArray [][] %s %i %i\n", arrayS, rowCount, colCount);

    // create MATLAB array
    T = mxCreateDoubleMatrix(rowCount, colCount, mxREAL);
    
	// get pointer on array
    tPtrR = mxGetPr(T);

	// copy array elements from java to MATLAB
    for (i=0; i<rowCount; i++) {
        //printf("row %i\n",i);
        colPtr = (*env)->GetObjectArrayElement(env, valueDD_JNI, i);
        //printf("got colPtr %i\n",i);
        
        arrayElements = (*env)->GetDoubleArrayElements(env, colPtr, 0);

        for (j=0; j<colCount; j++) {
            //printf("col %i\n",j);
            tPtrR[i + j*rowCount] = arrayElements[j];
        }

        //?? (suggestion of Dan Cervelli 13.09.2002) because of memory leak?
        (*env)->ReleaseDoubleArrayElements(env, colPtr, arrayElements, 0);

    } // rows


	// send array to MATLAB
    engPutVariable((Engine *)enginePtr, arrayS, T ); 
		
	// free memory for array
    mxDestroyArray(T);

	// free memory for String
    (*env)->ReleaseStringUTFChars(env, arrayS_JNI, arrayS); 
}


/**
 * double engGetScalarNATIVE(int epI, String nameS);
 * @param  
 * @param  
 * @return 
 */
JNIEXPORT jdouble JNICALL Java_jmatlink_CoreJMatLink_engGetScalarNATIVE
                      (JNIEnv *env, jobject obj, jlong enginePtr, jstring scalarS_JNI)
{
    mxArray  *arrayP;
    jdouble   scalar;
    
    // get name of scalar
    const char *scalarS = (*env)->GetStringUTFChars(env, scalarS_JNI, 0);

    if (debugB) printf("native engGetScalar %s \n",scalarS);

	// get variable from MATLAB
    arrayP = engGetVariable( (Engine *)enginePtr, scalarS);

    if (arrayP == NULL) {
        printf("Could not get scalar from MATLAB workspace.\n");
        (*env)->ReleaseStringUTFChars(env, scalarS_JNI, scalarS); // free memory
        return 0;
    }

	// get scalar value from array
    scalar = mxGetScalar(arrayP);

	// free memory 	
    mxDestroyArray(arrayP);                                   
    (*env)->ReleaseStringUTFChars(env, scalarS_JNI, scalarS); 
    
    return scalar;
}


/** double[][] engGetVariableNATIVE( int epI, String nameS )
 * @param  
 * @param  
 * @return 
 */ 
JNIEXPORT jobjectArray JNICALL Java_jmatlink_CoreJMatLink_engGetVariableNATIVE
                       (JNIEnv *env, jobject obj, jlong enginePtr, jstring arrayS_JNI)
{
    // NOTE: in java there are only 1-dimensional (array[]) arrays.
    //       higher dimensional arrays are arrays of arrays.
    jarray rowA;
    jclass rowClass;

    jarray columnA;
    int in,im;
    int m = 0;
    int n = 0;
  
    jdouble *rowElements;
    double *TP;
    mxArray  *arrayP;

    // convert array name to c-string
    const char *arrayS = (*env)->GetStringUTFChars(env, arrayS_JNI, 0);


    if (debugB) printf("native engGetArray %s \n",arrayS);

	// get variable from MATLAB's workspace
    arrayP = engGetVariable( (Engine *)enginePtr , arrayS);

    if (arrayP == NULL) 
    {
        printf("Could not get array %s from MATLAB workspace.\n", arrayS);
        (*env)->ReleaseStringUTFChars(env, arrayS_JNI, arrayS); // free memory
        return NULL;
    }

    m  = mxGetM( arrayP );   // rows 
    n  = mxGetN( arrayP );   // columns 
    TP = mxGetPr( arrayP );  // get pointer to values

 
  	/* create an array of double and get its class  */
    rowA        = (*env)->NewDoubleArray( env, n);              // row vector
    rowClass    = (*env)->GetObjectClass( env, rowA);           // row class

    /* create an array of object with the rowClass as the
       the default element */
    columnA = (*env)->NewObjectArray( env, m, rowClass, NULL);  // column vector

    for (im=0; im<m; im++) 
    {
      	rowA        = (*env)->NewDoubleArray( env, n);              // row vector
       	rowClass    = (*env)->GetObjectClass( env, rowA);           // row class
       	rowElements = (*env)->GetDoubleArrayElements(env, rowA, 0); // row elements

        for (in=0; in<n; in++) 
        {
           	rowElements[in] = TP[in*m + im];  
        }

        (*env)->SetObjectArrayElement(env, columnA, im, rowA);

        (*env)->ReleaseDoubleArrayElements(env, rowA, rowElements, 0); // free memory
    }
    

    mxDestroyArray(arrayP);                                 // free memory
    (*env)->ReleaseStringUTFChars(env, arrayS_JNI, arrayS); // free memory


    // are the following two line ok? return NULL ?!?!?
    if (enginePtr != NULL) return columnA;
    else                   return NULL;
}

/**
 * String[] engGetCharArrayNATIVE(String name)
 * @param  
 * @param  
 * @return 
 */
//JNIEXPORT jobjectArray JNICALL Java_jmatlink_CoreJMatLink_engGetCharArrayNATIVE
//                    (JNIEnv *env, jobject jthis, jlong enginePtr, jstring arrayS_JNI)
//{


//   const char *arrayS = (*env)->GetStringUTFChars(env, arrayS_JNI, 0);
//   mxArray    *arrayP;


//   if (debugB)  printf("native engGetArray %s \n",arrayS);

//   arrayP = engGetVariable( (Engine *)enginePtr, arrayS);

//   if (arrayP == NULL) {
//       printf("Could not get array %s from MATLAB workspace.\n", arrayS);
//       (*env)->ReleaseStringUTFChars(env, arrayS_JNI, arrayS); // free memory
//       return NULL;
//   }
   
//   if (mxIsChar(arrayP) != 0) {
//       printf("The array %s is not of type char.\n", arrayS);
//       (*env)->ReleaseStringUTFChars(env, arrayS_JNI, arrayS); // free memory
//       return NULL;
//   }


//   mxDestroyArray(arrayP);                                 // free memory
//   (*env)->ReleaseStringUTFChars(env, arrayS_JNI, arrayS); // free memory
//} // end engGetCharArrayNATIVE


/** void mexFunction(...)
 * @param  
 * @param  
 * @return 
 */
void mexFunction( int nlhs, mxArray *plhs[], int nrhs, const mxArray*prhs[] )
{
    return;
}
