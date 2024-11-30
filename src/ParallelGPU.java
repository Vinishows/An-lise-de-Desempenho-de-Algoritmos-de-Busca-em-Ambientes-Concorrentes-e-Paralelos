import org.jocl.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.jocl.CL.*;

public class ParallelGPU {
    public static int countWordOccurrences(String filePath, String targetWord) {
        CL.setExceptionsEnabled(true);

        String text;
        try {
            text = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            return -1;
        }

        byte[] textBytes = text.getBytes();
        byte[] wordBytes = targetWord.getBytes();
        int[] occurrences = new int[textBytes.length];

        cl_platform_id[] platforms = new cl_platform_id[1];
        clGetPlatformIDs(platforms.length, platforms, null);

        cl_device_id[] devices = new cl_device_id[1];
        clGetDeviceIDs(platforms[0], CL_DEVICE_TYPE_GPU, devices.length, devices, null);

        cl_context context = clCreateContext(null, 1, devices, null, null, null);
        cl_command_queue commandQueue = clCreateCommandQueueWithProperties(context, devices[0], null, null);

        String kernelSource;
        try {
            kernelSource = new String(Files.readAllBytes(Paths.get("resources/kernel.cl")));
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo do kernel: " + e.getMessage());
            return -1;
        }

        cl_program program = clCreateProgramWithSource(context, 1, new String[]{kernelSource}, null, null);
        clBuildProgram(program, 0, null, null, null, null);
        cl_kernel kernel = clCreateKernel(program, "countWord", null);

        cl_mem textBuffer = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_char * textBytes.length, Pointer.to(textBytes), null);
        cl_mem wordBuffer = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_char * wordBytes.length, Pointer.to(wordBytes), null);
        cl_mem occurrencesBuffer = clCreateBuffer(context, CL_MEM_WRITE_ONLY, Sizeof.cl_int * occurrences.length, null, null);

        clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(textBuffer));
        clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(wordBuffer));
        clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(occurrencesBuffer));
        clSetKernelArg(kernel, 3, Sizeof.cl_int, Pointer.to(new int[]{textBytes.length}));
        clSetKernelArg(kernel, 4, Sizeof.cl_int, Pointer.to(new int[]{wordBytes.length}));

        long[] globalWorkSize = new long[]{textBytes.length};
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, globalWorkSize, null, 0, null, null);
        clEnqueueReadBuffer(commandQueue, occurrencesBuffer, CL_TRUE, 0, Sizeof.cl_int * occurrences.length, Pointer.to(occurrences), 0, null, null);

        int totalOccurrences = 0;
        for (int count : occurrences) {
            totalOccurrences += count;
        }

        clReleaseMemObject(textBuffer);
        clReleaseMemObject(wordBuffer);
        clReleaseMemObject(occurrencesBuffer);
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);

        return totalOccurrences;
    }
}
