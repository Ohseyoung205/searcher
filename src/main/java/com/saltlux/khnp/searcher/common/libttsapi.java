package com.saltlux.khnp.searcher.common;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public final class libttsapi {
	
    private Socket serverSocket = null;
    private SocketAddress socketAddress = null;
    private OutputStream serverOutput = null;
    private BufferedInputStream serverInput = null;
    String host;
    int port;
    String text;
    public int nVoiceLength = 0;
    public byte[] szVoiceData;
    public int MarkSize = 0;
    public libttsapi.TTSMARK[] TTSMark;
    private int nConnectTimeout = 3000;
    private int nTimeout = 30000;
    private int nEnc = 0;
    static final String libttsapi_ver = "3.16.0.0";
    static final String SpecialBuild = "2020.06.30";
    static final int FILE_REQUEST = 0;
    static final int BUFF_REQUEST = 1;
    static final int STREAM_REQUEST = 2;
    static final int BUFF_REQUEST_SSML = 3;
    static final int BUFF_REQUEST_SYNC = 4;
    static final int FILE_REQUEST_SYNC = 5;
    static final int FILE_REQUEST_SYNC1 = 6;
    static final int BUFF_REQUEST_SYNC1 = 7;
    static final int IMPLICIT_REQUEST = 0;
    static final int EXPLICIT_REQUEST = -1;
    static final int REQUEST_TYPE_NONE = 0;
    static final int REQUEST_TYPE_TTS = 1;
    static final int REQUEST_TYPE_ASR = 2;
    static final int REQUEST_TYPE_TTS_ENC = 3;
    public static final int DEFAULT_TTS_PORT = 7000;
    public static final int TTS_HOSTNAME_ERROR = -1;
    public static final int TTS_SOCKET_ERROR = -2;
    public static final int TTS_CONNECT_ERROR = -3;
    public static final int TTS_READWRITE_ERROR = -4;
    public static final int TTS_MEMORY_ERROR = -5;
    public static final int TTS_TEXT_ERROR = -6;
    public static final int TTS_VOICEFORMAT_ERROR = -7;
    public static final int TTS_PARAM_ERROR = -8;
    public static final int TTS_RESULT_ERROR = -9;
    public static final int TTS_SPEAKER_ERROR = -10;
    public static final int TTS_DISK_ERROR = -11;
    public static final int TTS_UNKNOWN_ERROR = -12;
    public static final int TTS_SSML_ERROR = -13;
    public static final int TTS_ENC_ERROR = -14;
    public static final int TTS_ABNORMAL_ERROR = -15;
    public static final int TTS_MAX_ERROR = -100;
    public static final int TTS_RESULT_CONTINUE = 0;
    public static final int TTS_RESULT_SUCCESS = 1;
    public static final int TTS_SERVICE_ON = 1;
    public static final int TTS_SERVICE_OFF = 0;
    public static final int TTS_SERVICE_PAUSED = 2;
    public static final int FORMAT_DEFAULT = 0;
    public static final int FORMAT_WAV = 1;
    public static final int FORMAT_PCM = 2;
    public static final int FORMAT_MULAW = 3;
    public static final int FORMAT_ALAW = 4;
    public static final int FORMAT_ADPCM = 5;
    public static final int FORMAT_ASF = 6;
    public static final int FORMAT_WMA = 7;
    public static final int FORMAT_32ADPCM = 8;
    public static final int FORMAT_MP3 = 9;
    public static final int FORMAT_OGG = 10;
    public static final int FORMAT_8BITWAV = 11;
    public static final int FORMAT_AWAV = 12;
    public static final int FORMAT_MUWAV = 13;
    public static final int FORMAT_ADWAV = 14;
    public static final int FORMAT_G726 = 15;
    public static final int FORMAT_8BITPCM = 16;
    public static final int FORMAT_OUTPROC = 17;
    public static final int FORMAT_OUTPROC_SYNC = 18;
    static final int MAX_VOICE_FORMAT = 20;
    public static final int TEXT_NORMAL = 0;
    public static final int TEXT_SSML = 1;
    public static final int TEXT_UTF8 = 2;
    public static final int TEXT_JEITA = 4;
    public static final int TEXT_BIG5 = 6;
    static final int FORMAT_MULTIPLE_BUFFER = 0;
    static final int FORMAT_ONE_BUFFER = 1;
    static final int FORMAT_FILE = 2;
    static final int FORMAT_SSML_BUFFER = 3;
    static final int FORMAT_SYNC_FILE = 4;
    static final int FORMAT_SYNC_BUFFER = 5;
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    public static final int ENC_ON = 1;
    public static final int ENC_OFF = 0;
    public static final int MAX_MARK_NAME = 512;
    private byte[] nFormat = new byte[1];
    private byte[] szSaveDir = new byte[128];
    private byte[] szSaveFile = new byte[128];
    private byte[] BackMusicFile = new byte[32];
    private byte[] TextLength = new byte[32];
    private byte[] SpeakerID = new byte[4];
    private byte[] VoiceFormat = new byte[1];
    private byte[] TextFormat = new byte[1];
    private byte[] nConnType = new byte[1];
    private byte[] DictNum = new byte[1];
    private byte[] Volume = new byte[1];
    private byte[] Speed = new byte[1];
    private byte[] Pitch = new byte[1];
    private byte[] EffectID = new byte[1];
    private byte[] bAll = new byte[1];
    private byte[] reserved = new byte[32];
    private byte[] ReturnCode = new byte[1];
    private byte[] VoiceLength = new byte[32];
    private byte[] bEnd = new byte[1];
    private byte[] ServerIP = new byte[4];
    private byte[] reserved1 = new byte[8];
    private byte[] szkey = new byte[33];

    public libttsapi() {
    }

    public int GetEnc() {
        return this.nEnc;
    }

    public void SetEnc(int var1) {
        if (var1 == 1) {
            this.nEnc = var1;
        } else {
            this.nEnc = 0;
        }

    }

    public int ttsRequestFile(String var1, int var2, String var3, String var4, String var5, int var6, int var7) throws IOException {
        if (var3 != null && var3.length() > 0) {
            byte[] var8;
            if (var6 / 100 == 0) {
                var8 = var3.getBytes("EUC-KR");
            } else if (var6 / 100 != 1 && var6 / 100 != 5) {
                if (var6 / 100 != 2 && var6 / 100 != 7 && var6 / 100 != 8) {
                    if (var6 / 100 == 3) {
                        var8 = var3.getBytes("MS932");
                    } else if (var6 / 100 == 4) {
                        var8 = var3.getBytes("ISO-8859-1");
                    } else if (var6 / 100 != 6 && var6 / 100 != 10 && var6 / 100 != 11 && var6 / 100 != 12 && var6 / 100 != 13 && var6 / 100 != 14) {
                        if (var6 / 100 == 9) {
                            var8 = var3.getBytes("MS874");
                        } else {
                            var8 = var3.getBytes();
                        }
                    } else {
                        var8 = var3.getBytes("Cp1252");
                    }
                } else {
                    var8 = var3.getBytes("GBK");
                }
            } else {
                var8 = var3.getBytes("US-ASCII");
            }

            return this.ttsRequestFileEx(var1, var2, (byte[])var8, var4, var5, var6, var7, 0, -1, -1, -1, var6 / 100);
        } else {
            return -6;
        }
    }

    public int ttsRequestFile(String var1, int var2, byte[] var3, String var4, String var5, int var6, int var7) throws IOException {
        return this.ttsRequestFileEx(var1, var2, (byte[])var3, var4, var5, var6, var7, 0, -1, -1, -1, var6 / 100);
    }

    public int ttsRequestFile_r15(String var1, int var2, byte[] var3, String var4, String var5, String var6, int var7) throws IOException {
        return this.ttsRequestFileEx_r15(var1, var2, var3, var4, var5, var6, var7, 0, -1, -1, -1, -1);
    }

    public int ttsRequestFileSSML(String var1, int var2, String var3, String var4, String var5, int var6, int var7) throws IOException {
        if (var3 != null && var3.length() > 0) {
            byte[] var8 = var3.getBytes("UTF-8");
            return this.ttsRequestFileSSML(var1, var2, var8, var4, var5, var6, var7);
        } else {
            return -6;
        }
    }

    public int ttsRequestBuffer(String var1, int var2, String var3, int var4, int var5, int var6, int var7) throws IOException {
        if (var3 != null && var3.length() > 0) {
            byte[] var8;
            if (var4 / 100 == 0) {
                var8 = var3.getBytes("EUC-KR");
            } else if (var4 / 100 != 1 && var4 / 100 != 5) {
                if (var4 / 100 != 2 && var4 / 100 != 7 && var4 / 100 != 8) {
                    if (var4 / 100 == 3) {
                        var8 = var3.getBytes("MS932");
                    } else if (var4 / 100 == 4) {
                        var8 = var3.getBytes("ISO-8859-1");
                    } else if (var4 / 100 != 6 && var4 / 100 != 10 && var4 / 100 != 11 && var4 / 100 != 12 && var4 / 100 != 13 && var4 / 100 != 14) {
                        if (var4 / 100 == 9) {
                            var8 = var3.getBytes("MS874");
                        } else {
                            var8 = var3.getBytes();
                        }
                    } else {
                        var8 = var3.getBytes("Cp1252");
                    }
                } else {
                    var8 = var3.getBytes("GBK");
                }
            } else {
                var8 = var3.getBytes("US-ASCII");
            }

            return this.ttsRequestBufferEx(var1, var2, (byte[])var8, var4, var5, 0, -1, -1, -1, var4 / 100, var6, var7);
        } else {
            return -6;
        }
    }

    public int ttsRequestBuffer(String var1, int var2, byte[] var3, int var4, int var5, int var6, int var7) throws IOException {
        return this.ttsRequestBufferEx(var1, var2, (byte[])var3, var4, var5, 0, -1, -1, -1, var4 / 100, var6, var7);
    }

    public int ttsRequestBuffer_r15(String var1, int var2, byte[] var3, String var4, int var5, int var6, int var7) throws IOException {
        return this.ttsRequestBufferEx_r15(var1, var2, var3, var4, var5, 0, -1, -1, -1, -1, var6, var7);
    }

    public int ttsRequestFileEx(String var1, int var2, String var3, String var4, String var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) throws IOException {
        if (var3 != null && var3.length() > 0) {
            byte[] var13;
            if (var8 == 1) {
                var13 = var3.getBytes("UTF-8");
            } else if (var6 / 100 == 0) {
                var13 = var3.getBytes("EUC-KR");
            } else if (var6 / 100 != 1 && var6 / 100 != 5) {
                if (var6 / 100 != 2 && var6 / 100 != 7 && var6 / 100 != 8) {
                    if (var6 / 100 == 3) {
                        var13 = var3.getBytes("MS932");
                    } else if (var6 / 100 == 4) {
                        var13 = var3.getBytes("ISO-8859-1");
                    } else if (var6 / 100 != 6 && var6 / 100 != 10 && var6 / 100 != 11 && var6 / 100 != 12 && var6 / 100 != 13 && var6 / 100 != 14) {
                        if (var6 / 100 == 9) {
                            var13 = var3.getBytes("MS874");
                        } else {
                            var13 = var3.getBytes();
                        }
                    } else {
                        var13 = var3.getBytes("Cp1252");
                    }
                } else {
                    var13 = var3.getBytes("GBK");
                }
            } else {
                var13 = var3.getBytes("US-ASCII");
            }

            return this.ttsRequestFileEx(var1, var2, var13, var4, var5, var6, var7, var8, var9, var10, var11, var12);
        } else {
            return -6;
        }
    }

    public int ttsRequestBufferEx(String var1, int var2, String var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) throws IOException {
        if (var3 != null && var3.length() > 0) {
            byte[] var13;
            if (var6 == 1) {
                var13 = var3.getBytes("UTF-8");
            } else if (var4 / 100 == 0) {
                var13 = var3.getBytes("EUC-KR");
            } else if (var4 / 100 != 1 && var4 / 100 != 5) {
                if (var4 / 100 != 2 && var4 / 100 != 7 && var4 / 100 != 8) {
                    if (var4 / 100 == 3) {
                        var13 = var3.getBytes("MS932");
                    } else if (var4 / 100 == 4) {
                        var13 = var3.getBytes("ISO-8859-1");
                    } else if (var4 / 100 != 6 && var4 / 100 != 10 && var4 / 100 != 11 && var4 / 100 != 12 && var4 / 100 != 13 && var4 / 100 != 14) {
                        if (var4 / 100 == 9) {
                            var13 = var3.getBytes("MS874");
                        } else {
                            var13 = var3.getBytes();
                        }
                    } else {
                        var13 = var3.getBytes("Cp1252");
                    }
                } else {
                    var13 = var3.getBytes("GBK");
                }
            } else {
                var13 = var3.getBytes("US-ASCII");
            }

            return this.ttsRequestBufferEx(var1, var2, var13, var4, var5, var6, var7, var8, var9, var10, var11, var12);
        } else {
            return -6;
        }
    }

    public int ttsRequestBufferSSMLEx(String var1, int var2, String var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) throws IOException {
        if (var3 != null && var3.length() > 0) {
            byte[] var11 = var3.getBytes("UTF-8");
            return this.ttsRequestBufferSSMLEx(var1, var2, var11, var4, var5, var6, var7, var8, var9, var10);
        } else {
            return -6;
        }
    }

    private void init_parameter() {
        this.initByte(this.nFormat, 1);
        this.initByte(this.szSaveDir, 128);
        this.initByte(this.szSaveFile, 128);
        this.initByte(this.BackMusicFile, 32);
        this.initByte(this.TextLength, 32);
        this.initByte(this.SpeakerID, 4);
        this.initByte(this.VoiceFormat, 1);
        this.initByte(this.TextFormat, 1);
        this.initByte(this.nConnType, 1);
        this.initByte(this.DictNum, 1);
        this.initByte(this.Volume, 1);
        this.initByte(this.Speed, 1);
        this.initByte(this.Pitch, 1);
        this.initByte(this.EffectID, 1);
        this.initByte(this.bAll, 1);
        this.initByte(this.reserved, 32);
        this.initByte(this.ReturnCode, 1);
        this.initByte(this.VoiceLength, 32);
        this.initByte(this.bEnd, 1);
        this.initByte(this.ServerIP, 4);
        this.initByte(this.reserved1, 8);
        this.nVoiceLength = -1;
        this.szVoiceData = null;
        this.TTSMark = null;
        this.MarkSize = -1;
    }

    private int RequestHead_Send(OutputStream var1) {
        byte var2 = 0;

        try {
            var1.write(this.nFormat, 0, 1);
            int var6 = var2 + 1;
            var1.write(this.szSaveDir, 0, 128);
            var6 += 128;
            var1.write(this.szSaveFile, 0, 128);
            var6 += 128;
            var1.write(this.BackMusicFile, 0, 32);
            var6 += 32;
            var1.write(this.TextLength, 0, 32);
            var6 += 32;
            var1.write(this.SpeakerID, 0, 4);
            var6 += 4;
            var1.write(this.VoiceFormat, 0, 1);
            ++var6;
            var1.write(this.TextFormat, 0, 1);
            ++var6;
            var1.write(this.nConnType, 0, 1);
            ++var6;
            var1.write(this.DictNum, 0, 1);
            ++var6;
            var1.write(this.Volume, 0, 1);
            ++var6;
            var1.write(this.Speed, 0, 1);
            ++var6;
            var1.write(this.Pitch, 0, 1);
            ++var6;
            var1.write(this.EffectID, 0, 1);
            ++var6;
            var1.write(this.bAll, 0, 1);
            ++var6;
            var1.write(this.reserved, 0, 32);
            var6 += 32;
            var1.flush();
            return var6;
        } catch (IOException var4) {
            this.closeSocket();
            return -4;
        } catch (Exception var5) {
            this.closeSocket();
            return -4;
        }
    }

    private int ResultHead_Recv(BufferedInputStream var1) {
        try {
            var1.read(this.ReturnCode, 0, 1);
            var1.read(this.VoiceLength, 0, 32);
            var1.read(this.bEnd, 0, 1);
            var1.read(this.ServerIP, 0, 4);
            var1.read(this.reserved, 0, 8);
            return 0;
        } catch (IOException var3) {
            this.closeSocket();
            return -4;
        } catch (Exception var4) {
            this.closeSocket();
            return -4;
        }
    }

    public int ttsRequestFileSSML(String var1, int var2, byte[] var3, String var4, String var5, int var6, int var7) throws IOException {
        boolean var8 = false;
        boolean var9 = false;
        boolean var10 = false;
        byte[] var11 = new byte[1];
        int var12 = this.getByteLen(var3);
        var11[0] = 1;
        byte var13 = 1;
        byte var14 = -1;
        byte var15 = -1;
        byte var16 = -1;
        int var17 = var6 / 100;
        this.init_parameter();
        byte var30;
        if (var1 != null && var1.length() > 0) {
            int var31;
            if ((var31 = this.Check_Server(var1, var2)) != 0) {
                return var31;
            } else if ((var31 = this.Check_Voiceformat(var7, 2)) != 0) {
                return var31;
            } else if ((var31 = this.Check_dir_file_name(var4, var5, var7)) != 0) {
                return var31;
            } else if ((var31 = this.Check_Textformat(var13)) < 0) {
                return var31;
            } else {
                if (var4 != null) {
                    this.copyStringToByte(var4, this.szSaveDir);
                } else {
                    this.copyStringToByte("", this.szSaveDir);
                }

                if (var5 != null) {
                    this.copyStringToByte(var5, this.szSaveFile);
                } else {
                    var31 = -8;
                }

                int var34 = this.Check_Speed(var15);
                int var33 = this.Check_Volume(var14);
                int var35 = this.Check_Pitch(var16);
                var17 = this.Check_Dicnum(var17, var6);
                int var32 = this.Check_Textformat(var13);

                try {
                    this.socketAddress = new InetSocketAddress(var1, var2);
                    this.serverSocket = new Socket();
                    this.serverSocket.connect(this.socketAddress, this.nConnectTimeout);
                    this.serverSocket.setSoTimeout(this.nTimeout);
                    this.serverSocket.setTcpNoDelay(true);
                    this.serverOutput = this.serverSocket.getOutputStream();
                    this.serverInput = new BufferedInputStream(this.serverSocket.getInputStream());
                } catch (UnknownHostException var27) {
                    var31 = -1;
                } catch (IOException var28) {
                    var31 = -3;
                } catch (Exception var29) {
                    var31 = -3;
                }

                if (var31 < 0) {
                    this.closeSocket();
                    return var31;
                } else {
                    this.nFormat[0] = 5;
                    this.nConnType[0] = 0;
                    String var18 = new String();
                    var18 = String.valueOf(var12);
                    this.copyStringToByte(var18, this.TextLength);
                    if (var6 > 0 && var6 <= 999) {
                        var18 = String.valueOf(var6);
                        this.copyStringToByte(var18, this.SpeakerID);
                    } else {
                        var18 = String.valueOf(-3);
                        this.copyStringToByte(var18, this.SpeakerID);
                        var18 = String.valueOf(var6);
                        this.copyStringToByte(var18, this.reserved);
                    }

                    this.VoiceFormat[0] = (byte)var7;
                    this.TextFormat[0] = (byte)var32;
                    byte[] var19;
                    int var20;
                    if (var17 >= 0 && var17 <= 100) {
                        this.DictNum[0] = (byte)var17;
                    } else {
                        this.DictNum[0] = -4;
                        var19 = new byte[16];
                        var18 = String.valueOf(var17);
                        this.copyStringToByte(var18, var19);

                        for(var20 = 16; var20 < 32; ++var20) {
                            this.reserved[var20] = var19[var20 - 16];
                        }
                    }

                    this.Volume[0] = (byte)(var33 / 2);
                    this.Speed[0] = (byte)(var34 / 2);
                    this.Pitch[0] = (byte)(var35 / 2);
                    this.serverOutput.write(var11, 0, 1);
                    var31 = this.RequestHead_Send(this.serverOutput);
                    if (var31 < 0) {
                        return var31;
                    } else {
                        try {
                            this.serverOutput.write(var3, 0, var12);
                            this.serverOutput.flush();
                        } catch (IOException var25) {
                            this.closeSocket();
                            return -4;
                        } catch (Exception var26) {
                            this.closeSocket();
                            return -4;
                        }

                        var31 = this.ResultHead_Recv(this.serverInput);
                        if (var31 != 0) {
                            return var31;
                        } else {
                            this.nVoiceLength = this.getintValue(this.VoiceLength, 32);
                            var31 = this.getintFromByte(this.ReturnCode[0]);
                            if (this.nVoiceLength > 0) {
                                this.szVoiceData = new byte[this.nVoiceLength];
                                this.initByte(this.szVoiceData, this.nVoiceLength);
                                var19 = new byte[32];
                                if (this.readSocket(this.serverInput, var19, 32) != 32) {
                                    var30 = -4;
                                    this.closeSocket();
                                    return var30;
                                }

                                this.MarkSize = this.getintValue(var19, 32);
                                this.TTSMark = new libttsapi.TTSMARK[this.MarkSize + 1];

                                for(var20 = 0; var20 < this.MarkSize; ++var20) {
                                    byte[] var21 = new byte[32];
                                    byte[] var22 = new byte[32];
                                    byte[] var23 = new byte[32];
                                    byte[] var24 = new byte[512];
                                    if (this.readSocket(this.serverInput, var21, 32) != 32) {
                                        var30 = -4;
                                        this.closeSocket();
                                        return var30;
                                    }

                                    if (this.readSocket(this.serverInput, var22, 32) != 32) {
                                        var30 = -4;
                                        this.closeSocket();
                                        return var30;
                                    }

                                    if (this.readSocket(this.serverInput, var23, 32) != 32) {
                                        var30 = -4;
                                        this.closeSocket();
                                        return var30;
                                    }

                                    if (this.readSocket(this.serverInput, var24, 512) != 512) {
                                        var30 = -4;
                                        this.closeSocket();
                                        return var30;
                                    }

                                    this.TTSMark[var20] = new libttsapi.TTSMARK(this.getintValue(var21, 32), this.getintValue(var22, 32), this.getintValue(var23, 32), this.copyByteToString(var24));
                                }
                            }

                            if (var31 == 1) {
                                this.closeSocket();
                            }

                            return var31;
                        }
                    }
                }
            }
        } else {
            var30 = -1;
            return var30;
        }
    }

    public int ttsRequestFileSSML_r15(String var1, int var2, byte[] var3, String var4, String var5, String var6, int var7) throws IOException {
        boolean var8 = false;
        boolean var9 = false;
        boolean var10 = false;
        byte[] var11 = new byte[1];
        int var12 = this.getByteLen(var3);
        var11[0] = 1;
        byte var13 = 1;
        byte var14 = -1;
        byte var15 = -1;
        byte var16 = -1;
        byte var17 = -1;
        this.init_parameter();
        byte var30;
        if (var1 != null && var1.length() > 0) {
            if (var6 != null && var6.length() > 0 && var6.length() <= 31) {
                int var31;
                if ((var31 = this.Check_Server(var1, var2)) != 0) {
                    return var31;
                } else if ((var31 = this.Check_Voiceformat(var7, 2)) != 0) {
                    return var31;
                } else if ((var31 = this.Check_dir_file_name(var4, var5, var7)) != 0) {
                    return var31;
                } else {
                    if (var4 != null) {
                        this.copyStringToByte(var4, this.szSaveDir);
                    } else {
                        this.copyStringToByte("", this.szSaveDir);
                    }

                    if (var5 != null) {
                        this.copyStringToByte(var5, this.szSaveFile);
                    } else {
                        var31 = -8;
                    }

                    int var33 = this.Check_Speed(var15);
                    int var32 = this.Check_Volume(var14);
                    int var34 = this.Check_Pitch(var16);

                    try {
                        this.socketAddress = new InetSocketAddress(var1, var2);
                        this.serverSocket = new Socket();
                        this.serverSocket.connect(this.socketAddress, this.nConnectTimeout);
                        this.serverSocket.setSoTimeout(this.nTimeout);
                        this.serverSocket.setTcpNoDelay(true);
                        this.serverOutput = this.serverSocket.getOutputStream();
                        this.serverInput = new BufferedInputStream(this.serverSocket.getInputStream());
                    } catch (UnknownHostException var27) {
                        var31 = -1;
                    } catch (IOException var28) {
                        var31 = -3;
                    } catch (Exception var29) {
                        var31 = -3;
                    }

                    if (var31 < 0) {
                        this.closeSocket();
                        return var31;
                    } else {
                        this.nFormat[0] = 5;
                        this.nConnType[0] = 0;
                        String var18 = new String();
                        var18 = String.valueOf(var12);
                        this.copyStringToByte(var18, this.TextLength);
                        var18 = String.valueOf(-2);
                        this.copyStringToByte(var18, this.SpeakerID);
                        this.VoiceFormat[0] = (byte)var7;
                        this.TextFormat[0] = (byte)var13;
                        byte[] var19;
                        int var20;
                        if (var17 >= 0 && var17 <= 100) {
                            this.DictNum[0] = (byte)var17;
                        } else {
                            this.DictNum[0] = -4;
                            var19 = new byte[16];
                            var18 = String.valueOf(var17);
                            this.copyStringToByte(var18, var19);

                            for(var20 = 16; var20 < 32; ++var20) {
                                this.reserved[var20] = var19[var20 - 16];
                            }
                        }

                        this.Volume[0] = (byte)(var32 / 2);
                        this.Speed[0] = (byte)(var33 / 2);
                        this.Pitch[0] = (byte)(var34 / 2);
                        this.copyStringToByte(var6, this.reserved);
                        this.serverOutput.write(var11, 0, 1);
                        var31 = this.RequestHead_Send(this.serverOutput);
                        if (var31 < 0) {
                            return var31;
                        } else {
                            try {
                                this.serverOutput.write(var3, 0, var12);
                                this.serverOutput.flush();
                            } catch (IOException var25) {
                                this.closeSocket();
                                return -4;
                            } catch (Exception var26) {
                                this.closeSocket();
                                return -4;
                            }

                            var31 = this.ResultHead_Recv(this.serverInput);
                            if (var31 != 0) {
                                return var31;
                            } else {
                                this.nVoiceLength = this.getintValue(this.VoiceLength, 32);
                                var31 = this.getintFromByte(this.ReturnCode[0]);
                                if (this.nVoiceLength > 0) {
                                    this.szVoiceData = new byte[this.nVoiceLength];
                                    this.initByte(this.szVoiceData, this.nVoiceLength);
                                    var19 = new byte[32];
                                    if (this.readSocket(this.serverInput, var19, 32) != 32) {
                                        var30 = -4;
                                        this.closeSocket();
                                        return var30;
                                    }

                                    this.MarkSize = this.getintValue(var19, 32);
                                    this.TTSMark = new libttsapi.TTSMARK[this.MarkSize + 1];

                                    for(var20 = 0; var20 < this.MarkSize; ++var20) {
                                        byte[] var21 = new byte[32];
                                        byte[] var22 = new byte[32];
                                        byte[] var23 = new byte[32];
                                        byte[] var24 = new byte[512];
                                        if (this.readSocket(this.serverInput, var21, 32) != 32) {
                                            var30 = -4;
                                            this.closeSocket();
                                            return var30;
                                        }

                                        if (this.readSocket(this.serverInput, var22, 32) != 32) {
                                            var30 = -4;
                                            this.closeSocket();
                                            return var30;
                                        }

                                        if (this.readSocket(this.serverInput, var23, 32) != 32) {
                                            var30 = -4;
                                            this.closeSocket();
                                            return var30;
                                        }

                                        if (this.readSocket(this.serverInput, var24, 512) != 512) {
                                            var30 = -4;
                                            this.closeSocket();
                                            return var30;
                                        }

                                        this.TTSMark[var20] = new libttsapi.TTSMARK(this.getintValue(var21, 32), this.getintValue(var22, 32), this.getintValue(var23, 32), this.copyByteToString(var24));
                                    }
                                }

                                if (var31 == 1) {
                                    this.closeSocket();
                                }

                                return var31;
                            }
                        }
                    }
                }
            } else {
                var30 = -10;
                return var30;
            }
        } else {
            var30 = -1;
            return var30;
        }
    }

    public int ttsRequestFileEx(String var1, int var2, byte[] var3, String var4, String var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) throws IOException {
        boolean var13 = false;
        boolean var14 = false;
        boolean var15 = false;
        byte[] var16 = new byte[1];
        int var17 = this.getByteLen(var3);
        var16[0] = 1;
        this.init_parameter();
        if (var1 != null && var1.length() > 0) {
            int var27;
            if ((var27 = this.Check_Server(var1, var2)) != 0) {
                return var27;
            } else if ((var27 = this.Check_Voiceformat(var7, 2)) != 0) {
                return var27;
            } else if ((var27 = this.Check_dir_file_name(var4, var5, var7)) != 0) {
                return var27;
            } else if (var6 / 100 != 7 && var6 / 100 != 8 && var8 == 6) {
                return -8;
            } else {
                if (var4 != null) {
                    this.copyStringToByte(var4, this.szSaveDir);
                } else {
                    this.copyStringToByte("", this.szSaveDir);
                }

                if (var5 != null) {
                    this.copyStringToByte(var5, this.szSaveFile);
                } else {
                    var27 = -8;
                }

                var10 = this.Check_Speed(var10);
                var9 = this.Check_Volume(var9);
                var11 = this.Check_Pitch(var11);
                var12 = this.Check_Dicnum(var12, var6);
                var8 = this.Check_Textformat(var8);

                try {
                    this.socketAddress = new InetSocketAddress(var1, var2);
                    this.serverSocket = new Socket();
                    this.serverSocket.connect(this.socketAddress, this.nConnectTimeout);
                    this.serverSocket.setSoTimeout(this.nTimeout);
                    this.serverSocket.setTcpNoDelay(true);
                    this.serverOutput = this.serverSocket.getOutputStream();
                    this.serverInput = new BufferedInputStream(this.serverSocket.getInputStream());
                } catch (UnknownHostException var23) {
                    var27 = -1;
                } catch (IOException var24) {
                    var27 = -3;
                } catch (Exception var25) {
                    var27 = -3;
                }

                if (var27 < 0) {
                    this.closeSocket();
                    return var27;
                } else {
                    this.nFormat[0] = 0;
                    this.nConnType[0] = 0;
                    String var18 = new String();
                    var18 = String.valueOf(var17);
                    this.copyStringToByte(var18, this.TextLength);
                    if (var6 > 0 && var6 <= 999) {
                        var18 = String.valueOf(var6);
                        this.copyStringToByte(var18, this.SpeakerID);
                    } else {
                        var18 = String.valueOf(-3);
                        this.copyStringToByte(var18, this.SpeakerID);
                        var18 = String.valueOf(var6);
                        this.copyStringToByte(var18, this.reserved);
                    }

                    this.VoiceFormat[0] = (byte)var7;
                    this.TextFormat[0] = (byte)var8;
                    if (var12 >= 0 && var12 <= 100) {
                        this.DictNum[0] = (byte)var12;
                    } else {
                        this.DictNum[0] = -4;
                        byte[] var19 = new byte[16];
                        var18 = String.valueOf(var12);
                        this.copyStringToByte(var18, var19);

                        for(int var20 = 16; var20 < 32; ++var20) {
                            this.reserved[var20] = var19[var20 - 16];
                        }
                    }

                    this.Volume[0] = (byte)(var9 / 2);
                    this.Speed[0] = (byte)(var10 / 2);
                    this.Pitch[0] = (byte)(var11 / 2);
                    this.serverOutput.write(var16, 0, 1);
                    var27 = this.RequestHead_Send(this.serverOutput);
                    if (var27 < 0) {
                        return var27;
                    } else {
                        try {
                            this.serverOutput.write(var3, 0, var17);
                            this.serverOutput.flush();
                        } catch (IOException var21) {
                            this.closeSocket();
                            return -4;
                        } catch (Exception var22) {
                            this.closeSocket();
                            return -4;
                        }

                        var27 = this.ResultHead_Recv(this.serverInput);
                        if (var27 != 0) {
                            return var27;
                        } else {
                            var27 = this.getintFromByte(this.ReturnCode[0]);
                            this.closeSocket();
                            return var27;
                        }
                    }
                }
            }
        } else {
            byte var26 = -1;
            return var26;
        }
    }

    public int ttsRequestFileEx_r15(String var1, int var2, byte[] var3, String var4, String var5, String var6, int var7, int var8, int var9, int var10, int var11, int var12) throws IOException {
        boolean var13 = false;
        boolean var14 = false;
        boolean var15 = false;
        byte[] var16 = new byte[1];
        int var17 = this.getByteLen(var3);
        var16[0] = 1;
        this.init_parameter();
        byte var26;
        if (var1 != null && var1.length() > 0) {
            int var27;
            if ((var27 = this.Check_Server(var1, var2)) != 0) {
                return var27;
            } else if ((var27 = this.Check_Voiceformat(var7, 2)) != 0) {
                return var27;
            } else if ((var27 = this.Check_dir_file_name(var4, var5, var7)) != 0) {
                return var27;
            } else if (var6 != null && var6.length() > 0 && var6.length() <= 31) {
                if (var4 != null) {
                    this.copyStringToByte(var4, this.szSaveDir);
                } else {
                    this.copyStringToByte("", this.szSaveDir);
                }

                if (var5 != null) {
                    this.copyStringToByte(var5, this.szSaveFile);
                } else {
                    var27 = -8;
                }

                var10 = this.Check_Speed(var10);
                var9 = this.Check_Volume(var9);
                var11 = this.Check_Pitch(var11);

                try {
                    this.socketAddress = new InetSocketAddress(var1, var2);
                    this.serverSocket = new Socket();
                    this.serverSocket.connect(this.socketAddress, this.nConnectTimeout);
                    this.serverSocket.setSoTimeout(this.nTimeout);
                    this.serverSocket.setTcpNoDelay(true);
                    this.serverOutput = this.serverSocket.getOutputStream();
                    this.serverInput = new BufferedInputStream(this.serverSocket.getInputStream());
                } catch (UnknownHostException var23) {
                    var27 = -1;
                } catch (IOException var24) {
                    var27 = -3;
                } catch (Exception var25) {
                    var27 = -3;
                }

                if (var27 < 0) {
                    this.closeSocket();
                    return var27;
                } else {
                    this.nFormat[0] = 0;
                    this.nConnType[0] = 0;
                    String var18 = new String();
                    var18 = String.valueOf(var17);
                    this.copyStringToByte(var18, this.TextLength);
                    var18 = String.valueOf(-2);
                    this.copyStringToByte(var18, this.SpeakerID);
                    this.VoiceFormat[0] = (byte)var7;
                    this.TextFormat[0] = (byte)var8;
                    if (var12 >= 0 && var12 <= 100) {
                        this.DictNum[0] = (byte)var12;
                    } else {
                        this.DictNum[0] = -4;
                        byte[] var19 = new byte[16];
                        var18 = String.valueOf(var12);
                        this.copyStringToByte(var18, var19);

                        for(int var20 = 16; var20 < 32; ++var20) {
                            this.reserved[var20] = var19[var20 - 16];
                        }
                    }

                    this.Volume[0] = (byte)(var9 / 2);
                    this.Speed[0] = (byte)(var10 / 2);
                    this.Pitch[0] = (byte)(var11 / 2);
                    this.copyStringToByte(var6, this.reserved);
                    this.serverOutput.write(var16, 0, 1);
                    var27 = this.RequestHead_Send(this.serverOutput);
                    if (var27 < 0) {
                        return var27;
                    } else {
                        try {
                            this.serverOutput.write(var3, 0, var17);
                            this.serverOutput.flush();
                        } catch (IOException var21) {
                            this.closeSocket();
                            return -4;
                        } catch (Exception var22) {
                            this.closeSocket();
                            return -4;
                        }

                        var27 = this.ResultHead_Recv(this.serverInput);
                        if (var27 != 0) {
                            return var27;
                        } else {
                            var27 = this.getintFromByte(this.ReturnCode[0]);
                            this.closeSocket();
                            return var27;
                        }
                    }
                }
            } else {
                var26 = -10;
                return var26;
            }
        } else {
            var26 = -1;
            return var26;
        }
    }

    public int ttsRequestBufferEx(String var1, int var2, byte[] var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) throws IOException {
        boolean var13 = false;
        boolean var14 = false;
        byte[] var15 = new byte[1];
        int var16 = this.getByteLen(var3);
        var15[0] = 1;
        byte[] var17 = new byte[1];
        byte[] var18 = new byte[17];
        byte[] var19 = new byte[30];
        Object var20 = null;
        boolean var21 = true;
        boolean var22 = false;
        this.init_parameter();
        int var40;
        if ((var40 = this.Check_Server(var1, var2)) != 0) {
            return var40;
        } else if ((var40 = this.Check_Voiceformat(var5, var12)) != 0) {
            return var40;
        } else if ((var40 = this.Check_Textformat(var6)) < 0) {
            return var40;
        } else if (var4 / 100 != 7 && var4 / 100 != 8 && var6 == 6) {
            return -8;
        } else {
            var8 = this.Check_Speed(var8);
            var7 = this.Check_Volume(var7);
            var9 = this.Check_Pitch(var9);
            var10 = this.Check_Dicnum(var10, var4);
            var6 = this.Check_Textformat(var6);
            byte var41;
            if (var12 != 0 && var12 != 1) {
                var41 = -8;
                return var41;
            } else {
                AESVW var23;
                int var43;
                if (var11 == 1 || var12 == 1) {
                    try {
                        this.socketAddress = new InetSocketAddress(var1, var2);
                        this.serverSocket = new Socket();
                        this.serverSocket.connect(this.socketAddress, this.nConnectTimeout);
                        this.serverSocket.setSoTimeout(this.nTimeout);
                        this.serverSocket.setTcpNoDelay(true);
                        this.serverOutput = this.serverSocket.getOutputStream();
                        this.serverInput = new BufferedInputStream(this.serverSocket.getInputStream());
                    } catch (UnknownHostException var37) {
                        var40 = -1;
                    } catch (IOException var38) {
                        var40 = -3;
                    } catch (Exception var39) {
                        var40 = -3;
                    }

                    if (var40 < 0) {
                        this.closeSocket();
                        return var40;
                    }

                    if (this.serverSocket == null || this.serverOutput == null || this.serverInput == null) {
                        var41 = -2;
                        return var41;
                    }

                    byte[] var42;
                    if (this.nEnc == 1) {
                        var15[0] = 3;

                        try {
                            this.serverOutput.write(var15, 0, 1);
                        } catch (IOException var35) {
                            var41 = -4;
                            this.closeSocket();
                            return var41;
                        } catch (Exception var36) {
                            var41 = -4;
                            this.closeSocket();
                            return var41;
                        }

                        this.initByte(var19, 30);
                        if (this.readSocket(this.serverInput, var19, 30) != 30) {
                            var41 = -4;
                            this.closeSocket();
                            return var41;
                        }

                        if (var19[0] != 17 || var19[1] != 75 || var19[2] != 77 || var19[3] != 79 || var19[4] != 75) {
                            var41 = -15;
                            this.closeSocket();
                            return var41;
                        }

                        this.initByte(var18, 17);
                        if (this.readSocket(this.serverInput, var18, 16) != 16) {
                            var41 = -4;
                            this.closeSocket();
                            return var41;
                        }

                        var23 = new AESVW();
                        if ((this.szkey = var23.tea_genkey(var18)) == null) {
                            var41 = -14;
                            this.closeSocket();
                            return var41;
                        }

                        if ((var43 = var23.Encrypt(var3, var16, this.szkey, 256, 2)) <= 0) {
                            var41 = -14;
                            this.closeSocket();
                            return var41;
                        }

                        var42 = var23.enc_output;
                    } else {
                        var42 = var3;
                        var43 = var16;
                        var15[0] = 1;

                        try {
                            this.serverOutput.write(var15, 0, 1);
                        } catch (IOException var33) {
                            var41 = -4;
                            this.closeSocket();
                            return var41;
                        } catch (Exception var34) {
                            var41 = -4;
                            this.closeSocket();
                            return var41;
                        }
                    }

                    this.nFormat[0] = 1;
                    this.nConnType[0] = 0;
                    String var45 = new String();
                    var45 = String.valueOf(var43);
                    this.copyStringToByte(var45, this.TextLength);
                    if (var4 > 0 && var4 <= 999) {
                        var45 = String.valueOf(var4);
                        this.copyStringToByte(var45, this.SpeakerID);
                    } else {
                        var45 = String.valueOf(-3);
                        this.copyStringToByte(var45, this.SpeakerID);
                        var45 = String.valueOf(var4);
                        this.copyStringToByte(var45, this.reserved);
                    }

                    this.VoiceFormat[0] = (byte)var5;
                    this.TextFormat[0] = (byte)var6;
                    if (var10 >= 0 && var10 <= 100) {
                        this.DictNum[0] = (byte)var10;
                    } else {
                        this.DictNum[0] = -4;
                        byte[] var24 = new byte[16];
                        var45 = String.valueOf(var10);
                        this.copyStringToByte(var45, var24);

                        for(int var25 = 16; var25 < 32; ++var25) {
                            this.reserved[var25] = var24[var25 - 16];
                        }
                    }

                    this.Volume[0] = (byte)(var7 / 2);
                    this.Speed[0] = (byte)(var8 / 2);
                    this.Pitch[0] = (byte)(var9 / 2);
                    if (var12 == 1) {
                        this.bAll[0] = 1;
                    } else {
                        this.bAll[0] = 0;
                    }

                    var40 = this.RequestHead_Send(this.serverOutput);
                    if (var40 < 0) {
                        return var40;
                    }

                    try {
                        this.serverOutput.write(var42, 0, var43);
                        this.serverOutput.flush();
                    } catch (IOException var31) {
                        var41 = -4;
                        this.closeSocket();
                        return var41;
                    } catch (Exception var32) {
                        var41 = -4;
                        this.closeSocket();
                        return var41;
                    }
                }

                if (var11 == 2 && var12 == 0) {
                    var17[0] = 1;

                    try {
                        this.serverOutput.write(var17, 0, 1);
                    } catch (IOException var26) {
                        var41 = -4;
                        this.closeSocket();
                        return var41;
                    } catch (Exception var27) {
                        var41 = -4;
                        this.closeSocket();
                        return var41;
                    }

                    var14 = true;
                    this.szVoiceData = null;
                    this.nVoiceLength = 0;
                    byte var44 = 1;
                    this.closeSocket();
                    return var44;
                } else {
                    if (var11 != 2 && var12 == 0) {
                        var17[0] = 0;

                        try {
                            this.serverOutput.write(var17, 0, 1);
                        } catch (IOException var29) {
                            var41 = -4;
                            this.closeSocket();
                            return var41;
                        } catch (Exception var30) {
                            var41 = -4;
                            this.closeSocket();
                            return var41;
                        }
                    }

                    var40 = this.ResultHead_Recv(this.serverInput);
                    if (var40 != 0) {
                        return var40;
                    } else {
                        this.nVoiceLength = this.getintValue(this.VoiceLength, 32);
                        var40 = this.getintFromByte(this.ReturnCode[0]);
                        if (this.bEnd[0] == 1) {
                            var14 = true;
                        } else {
                            var14 = false;
                        }

                        if (this.nVoiceLength > 0) {
                            try {
                                this.szVoiceData = new byte[this.nVoiceLength];
                                this.initByte(this.szVoiceData, this.nVoiceLength);
                            } catch (Exception var28) {
                                System.out.println(var28.toString());
                            }

                            if (this.readSocket(this.serverInput, this.szVoiceData, this.nVoiceLength) != this.nVoiceLength) {
                                var41 = -4;
                                this.closeSocket();
                                return var41;
                            }

                            if (this.nEnc == 1) {
                                var23 = new AESVW();
                                if ((var43 = var23.Decrypt(this.szVoiceData, this.nVoiceLength, this.szkey, 256, 2)) <= 0) {
                                    var41 = -14;
                                    this.closeSocket();
                                    return var41;
                                }

                                this.nVoiceLength = var43;
                                this.szVoiceData = new byte[this.nVoiceLength];
                                this.initByte(this.szVoiceData, this.nVoiceLength);
                                this.copyByte(this.szVoiceData, var23.dec_output, this.nVoiceLength);
                            }

                            if (var14) {
                                var40 = 1;
                            } else {
                                var40 = 0;
                            }
                        }

                        if (var40 == 1) {
                            this.closeSocket();
                        }

                        return var40;
                    }
                }
            }
        }
    }

    public int ttsRequestBufferEx_r15(String var1, int var2, byte[] var3, String var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) throws IOException {
        boolean var13 = false;
        boolean var14 = false;
        byte[] var15 = new byte[1];
        int var16 = this.getByteLen(var3);
        var15[0] = 1;
        byte[] var17 = new byte[1];
        byte[] var18 = new byte[17];
        byte[] var19 = new byte[30];
        Object var20 = null;
        boolean var21 = true;
        boolean var22 = false;
        this.init_parameter();
        int var40;
        if ((var40 = this.Check_Server(var1, var2)) != 0) {
            return var40;
        } else if ((var40 = this.Check_Voiceformat(var5, var12)) != 0) {
            return var40;
        } else if ((var40 = this.Check_Textformat(var6)) < 0) {
            return var40;
        } else {
            byte var41;
            if (var4 != null && var4.length() > 0 && var4.length() <= 31) {
                var8 = this.Check_Speed(var8);
                var7 = this.Check_Volume(var7);
                var9 = this.Check_Pitch(var9);
                if (var12 != 0 && var12 != 1) {
                    var41 = -8;
                    return var41;
                } else {
                    AESVW var23;
                    int var43;
                    if (var11 == 1 || var12 == 1) {
                        try {
                            this.socketAddress = new InetSocketAddress(var1, var2);
                            this.serverSocket = new Socket();
                            this.serverSocket.connect(this.socketAddress, this.nConnectTimeout);
                            this.serverSocket.setSoTimeout(this.nTimeout);
                            this.serverSocket.setTcpNoDelay(true);
                            this.serverOutput = this.serverSocket.getOutputStream();
                            this.serverInput = new BufferedInputStream(this.serverSocket.getInputStream());
                        } catch (UnknownHostException var37) {
                            var40 = -1;
                        } catch (IOException var38) {
                            var40 = -3;
                        } catch (Exception var39) {
                            var40 = -3;
                        }

                        if (var40 < 0) {
                            this.closeSocket();
                            return var40;
                        }

                        if (this.serverSocket == null || this.serverOutput == null || this.serverInput == null) {
                            var41 = -2;
                            return var41;
                        }

                        byte[] var42;
                        if (this.nEnc == 1) {
                            var15[0] = 3;

                            try {
                                this.serverOutput.write(var15, 0, 1);
                            } catch (IOException var35) {
                                var41 = -4;
                                this.closeSocket();
                                return var41;
                            } catch (Exception var36) {
                                var41 = -4;
                                this.closeSocket();
                                return var41;
                            }

                            this.initByte(var19, 30);
                            if (this.readSocket(this.serverInput, var19, 30) != 30) {
                                var41 = -4;
                                this.closeSocket();
                                return var41;
                            }

                            if (var19[0] != 17 || var19[1] != 75 || var19[2] != 77 || var19[3] != 79 || var19[4] != 75) {
                                var41 = -15;
                                this.closeSocket();
                                return var41;
                            }

                            this.initByte(var18, 17);
                            if (this.readSocket(this.serverInput, var18, 16) != 16) {
                                var41 = -4;
                                this.closeSocket();
                                return var41;
                            }

                            var23 = new AESVW();
                            if ((this.szkey = var23.tea_genkey(var18)) == null) {
                                var41 = -14;
                                this.closeSocket();
                                return var41;
                            }

                            if ((var43 = var23.Encrypt(var3, var16, this.szkey, 256, 2)) <= 0) {
                                var41 = -14;
                                this.closeSocket();
                                return var41;
                            }

                            var42 = var23.enc_output;
                        } else {
                            var42 = var3;
                            var43 = var16;
                            var15[0] = 1;

                            try {
                                this.serverOutput.write(var15, 0, 1);
                            } catch (IOException var33) {
                                var41 = -4;
                                this.closeSocket();
                                return var41;
                            } catch (Exception var34) {
                                var41 = -4;
                                this.closeSocket();
                                return var41;
                            }
                        }

                        this.nFormat[0] = 1;
                        this.nConnType[0] = 0;
                        String var45 = new String();
                        var45 = String.valueOf(var43);
                        this.copyStringToByte(var45, this.TextLength);
                        var45 = String.valueOf(-2);
                        this.copyStringToByte(var45, this.SpeakerID);
                        this.VoiceFormat[0] = (byte)var5;
                        this.TextFormat[0] = (byte)var6;
                        if (var10 >= 0 && var10 <= 100) {
                            this.DictNum[0] = (byte)var10;
                        } else {
                            this.DictNum[0] = -4;
                            byte[] var24 = new byte[16];
                            var45 = String.valueOf(var10);
                            this.copyStringToByte(var45, var24);

                            for(int var25 = 16; var25 < 32; ++var25) {
                                this.reserved[var25] = var24[var25 - 16];
                            }
                        }

                        this.Volume[0] = (byte)(var7 / 2);
                        this.Speed[0] = (byte)(var8 / 2);
                        this.Pitch[0] = (byte)(var9 / 2);
                        if (var12 == 1) {
                            this.bAll[0] = 1;
                        } else {
                            this.bAll[0] = 0;
                        }

                        this.copyStringToByte(var4, this.reserved);
                        var40 = this.RequestHead_Send(this.serverOutput);
                        if (var40 < 0) {
                            return var40;
                        }

                        try {
                            this.serverOutput.write(var42, 0, var43);
                            this.serverOutput.flush();
                        } catch (IOException var31) {
                            var41 = -4;
                            this.closeSocket();
                            return var41;
                        } catch (Exception var32) {
                            var41 = -4;
                            this.closeSocket();
                            return var41;
                        }
                    }

                    if (var11 == 2 && var12 == 0) {
                        var17[0] = 1;

                        try {
                            this.serverOutput.write(var17, 0, 1);
                        } catch (IOException var26) {
                            var41 = -4;
                            this.closeSocket();
                            return var41;
                        } catch (Exception var27) {
                            var41 = -4;
                            this.closeSocket();
                            return var41;
                        }

                        var14 = true;
                        this.szVoiceData = null;
                        this.nVoiceLength = 0;
                        byte var44 = 1;
                        this.closeSocket();
                        return var44;
                    } else {
                        if (var11 != 2 && var12 == 0) {
                            var17[0] = 0;

                            try {
                                this.serverOutput.write(var17, 0, 1);
                            } catch (IOException var29) {
                                var41 = -4;
                                this.closeSocket();
                                return var41;
                            } catch (Exception var30) {
                                var41 = -4;
                                this.closeSocket();
                                return var41;
                            }
                        }

                        var40 = this.ResultHead_Recv(this.serverInput);
                        if (var40 != 0) {
                            return var40;
                        } else {
                            this.nVoiceLength = this.getintValue(this.VoiceLength, 32);
                            var40 = this.getintFromByte(this.ReturnCode[0]);
                            if (this.bEnd[0] == 1) {
                                var14 = true;
                            } else {
                                var14 = false;
                            }

                            if (this.nVoiceLength > 0) {
                                try {
                                    this.szVoiceData = new byte[this.nVoiceLength];
                                    this.initByte(this.szVoiceData, this.nVoiceLength);
                                } catch (Exception var28) {
                                    System.out.println(var28.toString());
                                }

                                if (this.readSocket(this.serverInput, this.szVoiceData, this.nVoiceLength) != this.nVoiceLength) {
                                    var41 = -4;
                                    this.closeSocket();
                                    return var41;
                                }

                                if (this.nEnc == 1) {
                                    var23 = new AESVW();
                                    if ((var43 = var23.Decrypt(this.szVoiceData, this.nVoiceLength, this.szkey, 256, 2)) <= 0) {
                                        var41 = -14;
                                        this.closeSocket();
                                        return var41;
                                    }

                                    this.nVoiceLength = var43;
                                    this.szVoiceData = new byte[this.nVoiceLength];
                                    this.initByte(this.szVoiceData, this.nVoiceLength);
                                    this.copyByte(this.szVoiceData, var23.dec_output, this.nVoiceLength);
                                }

                                if (var14) {
                                    var40 = 1;
                                } else {
                                    var40 = 0;
                                }
                            }

                            if (var40 == 1) {
                                this.closeSocket();
                            }

                            return var40;
                        }
                    }
                }
            } else {
                var41 = -10;
                return var41;
            }
        }
    }

    public int ttsRequestBufferSSMLEx(String var1, int var2, byte[] var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) throws IOException {
        boolean var11 = false;
        boolean var12 = false;
        byte var13 = 1;
        boolean var14 = false;
        byte[] var15 = new byte[]{1};
        byte[] var16 = new byte[17];
        byte[] var17 = new byte[30];
        Object var18 = null;
        boolean var19 = true;
        boolean var20 = false;
        int var21 = var3.length;
        this.init_parameter();
        int var40;
        if ((var40 = this.Check_Server(var1, var2)) != 0) {
            return var40;
        } else if ((var40 = this.Check_Voiceformat(var5, 3)) != 0) {
            return var40;
        } else {
            byte var38 = 100;
            byte var37 = 100;
            byte var39 = 100;
            var9 = var4 / 100;
            AESVW var22;
            byte var41;
            int var43;
            if (var10 == 1 || var12) {
                try {
                    this.socketAddress = new InetSocketAddress(var1, var2);
                    this.serverSocket = new Socket();
                    this.serverSocket.connect(this.socketAddress, this.nConnectTimeout);
                    this.serverSocket.setSoTimeout(this.nTimeout);
                    this.serverSocket.setTcpNoDelay(true);
                    this.serverOutput = this.serverSocket.getOutputStream();
                    this.serverInput = new BufferedInputStream(this.serverSocket.getInputStream());
                } catch (UnknownHostException var34) {
                    var40 = -1;
                } catch (IOException var35) {
                    var40 = -3;
                } catch (Exception var36) {
                    var40 = -3;
                }

                if (var40 < 0) {
                    this.closeSocket();
                    return var40;
                }

                if (this.serverSocket == null || this.serverOutput == null || this.serverInput == null) {
                    var41 = -2;
                    this.closeSocket();
                    return var41;
                }

                byte[] var42;
                if (this.nEnc == 1) {
                    var15[0] = 3;

                    try {
                        this.serverOutput.write(var15, 0, 1);
                    } catch (IOException var32) {
                        var41 = -4;
                        this.closeSocket();
                        return var41;
                    } catch (Exception var33) {
                        var41 = -4;
                        this.closeSocket();
                        return var41;
                    }

                    this.initByte(var17, 30);
                    if (this.readSocket(this.serverInput, var17, 30) != 30) {
                        var41 = -4;
                        this.closeSocket();
                        return var41;
                    }

                    if (var17[0] != 17 || var17[1] != 75 || var17[2] != 77 || var17[3] != 79 || var17[4] != 75) {
                        var41 = -15;
                        this.closeSocket();
                        return var41;
                    }

                    this.initByte(var16, 17);
                    if (this.readSocket(this.serverInput, var16, 16) != 16) {
                        var41 = -4;
                        this.closeSocket();
                        return var41;
                    }

                    var22 = new AESVW();
                    if ((this.szkey = var22.tea_genkey(var16)) == null) {
                        var41 = -14;
                        this.closeSocket();
                        return var41;
                    }

                    if ((var43 = var22.Encrypt(var3, var21, this.szkey, 256, 2)) <= 0) {
                        var41 = -14;
                        this.closeSocket();
                        return var41;
                    }

                    var42 = var22.enc_output;
                } else {
                    var42 = var3;
                    var43 = var21;
                    var15[0] = 1;

                    try {
                        this.serverOutput.write(var15, 0, 1);
                    } catch (IOException var30) {
                        var41 = -4;
                        this.closeSocket();
                        return var41;
                    } catch (Exception var31) {
                        var41 = -4;
                        this.closeSocket();
                        return var41;
                    }
                }

                this.nFormat[0] = 3;
                this.nConnType[0] = 0;
                String var44 = new String();
                var44 = String.valueOf(var43);
                this.copyStringToByte(var44, this.TextLength);
                if (var4 > 0 && var4 <= 999) {
                    var44 = String.valueOf(var4);
                    this.copyStringToByte(var44, this.SpeakerID);
                } else {
                    var44 = String.valueOf(-3);
                    this.copyStringToByte(var44, this.SpeakerID);
                    var44 = String.valueOf(var4);
                    this.copyStringToByte(var44, this.reserved);
                }

                this.VoiceFormat[0] = (byte)var5;
                this.TextFormat[0] = (byte)var13;
                if (var9 >= 0 && var9 <= 100) {
                    this.DictNum[0] = (byte)var9;
                } else {
                    this.DictNum[0] = -4;
                    byte[] var23 = new byte[16];
                    var44 = String.valueOf(var9);
                    this.copyStringToByte(var44, var23);

                    for(int var24 = 16; var24 < 32; ++var24) {
                        this.reserved[var24] = var23[var24 - 16];
                    }
                }

                this.Volume[0] = (byte)(var37 / 2);
                this.Speed[0] = (byte)(var38 / 2);
                this.Pitch[0] = (byte)(var39 / 2);
                if (var12) {
                    this.bAll[0] = 1;
                } else {
                    this.bAll[0] = 0;
                }

                var40 = this.RequestHead_Send(this.serverOutput);
                if (var40 < 0) {
                    return var40;
                }

                try {
                    this.serverOutput.write(var42, 0, var43);
                    this.serverOutput.flush();
                } catch (IOException var28) {
                    var41 = -4;
                    this.closeSocket();
                    return var41;
                } catch (Exception var29) {
                    var41 = -4;
                    this.closeSocket();
                    return var41;
                }
            }

            var40 = this.ResultHead_Recv(this.serverInput);
            if (var40 != 0) {
                return var40;
            } else {
                this.nVoiceLength = this.getintValue(this.VoiceLength, 32);
                var40 = this.getintFromByte(this.ReturnCode[0]);
                if (this.bEnd[0] == 1) {
                    var14 = true;
                } else {
                    var14 = false;
                }

                if (this.nVoiceLength > 0) {
                    this.szVoiceData = new byte[this.nVoiceLength];
                    this.initByte(this.szVoiceData, this.nVoiceLength);
                    if (this.readSocket(this.serverInput, this.szVoiceData, this.nVoiceLength) != this.nVoiceLength) {
                        var41 = -4;
                        this.closeSocket();
                        return var41;
                    }

                    if (this.nEnc == 1) {
                        var22 = new AESVW();
                        if ((var43 = var22.Decrypt(this.szVoiceData, this.nVoiceLength, this.szkey, 256, 2)) <= 0) {
                            var41 = -14;
                            this.closeSocket();
                            return var41;
                        }

                        this.nVoiceLength = var43;
                        this.szVoiceData = new byte[this.nVoiceLength];
                        this.initByte(this.szVoiceData, this.nVoiceLength);
                        this.copyByte(this.szVoiceData, var22.dec_output, this.nVoiceLength);
                    }

                    byte[] var46 = new byte[32];
                    if (this.readSocket(this.serverInput, var46, 32) != 32) {
                        var41 = -4;
                        this.closeSocket();
                        return var41;
                    }

                    this.MarkSize = this.getintValue(var46, 32);
                    this.TTSMark = new libttsapi.TTSMARK[this.MarkSize + 1];

                    for(int var45 = 0; var45 < this.MarkSize; ++var45) {
                        byte[] var47 = new byte[32];
                        byte[] var25 = new byte[32];
                        byte[] var26 = new byte[32];
                        byte[] var27 = new byte[512];
                        if (this.readSocket(this.serverInput, var47, 32) != 32) {
                            var41 = -4;
                            this.closeSocket();
                            return var41;
                        }

                        if (this.readSocket(this.serverInput, var25, 32) != 32) {
                            var41 = -4;
                            this.closeSocket();
                            return var41;
                        }

                        if (this.readSocket(this.serverInput, var26, 32) != 32) {
                            var41 = -4;
                            this.closeSocket();
                            return var41;
                        }

                        if (this.readSocket(this.serverInput, var27, 512) != 512) {
                            var41 = -4;
                            this.closeSocket();
                            return var41;
                        }

                        this.TTSMark[var45] = new libttsapi.TTSMARK(this.getintValue(var47, 32), this.getintValue(var25, 32), this.getintValue(var26, 32), this.copyByteToString(var27));
                    }

                    if (var14) {
                        var40 = 1;
                    } else {
                        var40 = 0;
                    }
                }

                if (var40 == 1) {
                    this.closeSocket();
                }

                return var40;
            }
        }
    }

    public int ttsRequestBufferSSMLEx_r15(String var1, int var2, byte[] var3, String var4, int var5, int var6, int var7, int var8, int var9, int var10) throws IOException {
        boolean var11 = false;
        boolean var12 = false;
        byte var13 = 1;
        boolean var14 = false;
        byte[] var15 = new byte[]{1};
        byte[] var16 = new byte[17];
        byte[] var17 = new byte[30];
        Object var18 = null;
        boolean var19 = true;
        boolean var20 = false;
        int var21 = var3.length;
        this.init_parameter();
        int var41;
        if ((var41 = this.Check_Server(var1, var2)) != 0) {
            return var41;
        } else if ((var41 = this.Check_Voiceformat(var5, 3)) != 0) {
            return var41;
        } else {
            byte var42;
            if (var4 != null && var4.length() > 0 && var4.length() <= 31) {
                byte var38 = 100;
                byte var37 = 100;
                byte var39 = 100;
                byte var40 = -1;
                AESVW var22;
                int var44;
                if (var10 == 1 || var12) {
                    try {
                        this.socketAddress = new InetSocketAddress(var1, var2);
                        this.serverSocket = new Socket();
                        this.serverSocket.connect(this.socketAddress, this.nConnectTimeout);
                        this.serverSocket.setSoTimeout(this.nTimeout);
                        this.serverSocket.setTcpNoDelay(true);
                        this.serverOutput = this.serverSocket.getOutputStream();
                        this.serverInput = new BufferedInputStream(this.serverSocket.getInputStream());
                    } catch (UnknownHostException var34) {
                        var41 = -1;
                    } catch (IOException var35) {
                        var41 = -3;
                    } catch (Exception var36) {
                        var41 = -3;
                    }

                    if (var41 < 0) {
                        this.closeSocket();
                        return var41;
                    }

                    if (this.serverSocket == null || this.serverOutput == null || this.serverInput == null) {
                        var42 = -2;
                        this.closeSocket();
                        return var42;
                    }

                    byte[] var43;
                    if (this.nEnc == 1) {
                        var15[0] = 3;

                        try {
                            this.serverOutput.write(var15, 0, 1);
                        } catch (IOException var32) {
                            var42 = -4;
                            this.closeSocket();
                            return var42;
                        } catch (Exception var33) {
                            var42 = -4;
                            this.closeSocket();
                            return var42;
                        }

                        this.initByte(var17, 30);
                        if (this.readSocket(this.serverInput, var17, 30) != 30) {
                            var42 = -4;
                            this.closeSocket();
                            return var42;
                        }

                        if (var17[0] != 17 || var17[1] != 75 || var17[2] != 77 || var17[3] != 79 || var17[4] != 75) {
                            var42 = -15;
                            this.closeSocket();
                            return var42;
                        }

                        this.initByte(var16, 17);
                        if (this.readSocket(this.serverInput, var16, 16) != 16) {
                            var42 = -4;
                            this.closeSocket();
                            return var42;
                        }

                        var22 = new AESVW();
                        if ((this.szkey = var22.tea_genkey(var16)) == null) {
                            var42 = -14;
                            this.closeSocket();
                            return var42;
                        }

                        if ((var44 = var22.Encrypt(var3, var21, this.szkey, 256, 2)) <= 0) {
                            var42 = -14;
                            this.closeSocket();
                            return var42;
                        }

                        var43 = var22.enc_output;
                    } else {
                        var43 = var3;
                        var44 = var21;
                        var15[0] = 1;

                        try {
                            this.serverOutput.write(var15, 0, 1);
                        } catch (IOException var30) {
                            var42 = -4;
                            this.closeSocket();
                            return var42;
                        } catch (Exception var31) {
                            var42 = -4;
                            this.closeSocket();
                            return var42;
                        }
                    }

                    this.nFormat[0] = 3;
                    this.nConnType[0] = 0;
                    String var45 = new String();
                    var45 = String.valueOf(var44);
                    this.copyStringToByte(var45, this.TextLength);
                    var45 = String.valueOf(-2);
                    this.copyStringToByte(var45, this.SpeakerID);
                    this.VoiceFormat[0] = (byte)var5;
                    this.TextFormat[0] = (byte)var13;
                    if (var40 >= 0 && var40 <= 100) {
                        this.DictNum[0] = (byte)var40;
                    } else {
                        this.DictNum[0] = -4;
                        byte[] var23 = new byte[16];
                        var45 = String.valueOf(var40);
                        this.copyStringToByte(var45, var23);

                        for(int var24 = 16; var24 < 32; ++var24) {
                            this.reserved[var24] = var23[var24 - 16];
                        }
                    }

                    this.Volume[0] = (byte)(var37 / 2);
                    this.Speed[0] = (byte)(var38 / 2);
                    this.Pitch[0] = (byte)(var39 / 2);
                    if (var12) {
                        this.bAll[0] = 1;
                    } else {
                        this.bAll[0] = 0;
                    }

                    this.copyStringToByte(var4, this.reserved);
                    var41 = this.RequestHead_Send(this.serverOutput);
                    if (var41 < 0) {
                        return var41;
                    }

                    try {
                        this.serverOutput.write(var43, 0, var44);
                        this.serverOutput.flush();
                    } catch (IOException var28) {
                        var42 = -4;
                        this.closeSocket();
                        return var42;
                    } catch (Exception var29) {
                        var42 = -4;
                        this.closeSocket();
                        return var42;
                    }
                }

                var41 = this.ResultHead_Recv(this.serverInput);
                if (var41 != 0) {
                    return var41;
                } else {
                    this.nVoiceLength = this.getintValue(this.VoiceLength, 32);
                    var41 = this.getintFromByte(this.ReturnCode[0]);
                    if (this.bEnd[0] == 1) {
                        var14 = true;
                    } else {
                        var14 = false;
                    }

                    if (this.nVoiceLength > 0) {
                        this.szVoiceData = new byte[this.nVoiceLength];
                        this.initByte(this.szVoiceData, this.nVoiceLength);
                        if (this.readSocket(this.serverInput, this.szVoiceData, this.nVoiceLength) != this.nVoiceLength) {
                            var42 = -4;
                            this.closeSocket();
                            return var42;
                        }

                        if (this.nEnc == 1) {
                            var22 = new AESVW();
                            if ((var44 = var22.Decrypt(this.szVoiceData, this.nVoiceLength, this.szkey, 256, 2)) <= 0) {
                                var42 = -14;
                                this.closeSocket();
                                return var42;
                            }

                            this.nVoiceLength = var44;
                            this.szVoiceData = new byte[this.nVoiceLength];
                            this.initByte(this.szVoiceData, this.nVoiceLength);
                            this.copyByte(this.szVoiceData, var22.dec_output, this.nVoiceLength);
                        }

                        byte[] var47 = new byte[32];
                        if (this.readSocket(this.serverInput, var47, 32) != 32) {
                            var42 = -4;
                            this.closeSocket();
                            return var42;
                        }

                        this.MarkSize = this.getintValue(var47, 32);
                        this.TTSMark = new libttsapi.TTSMARK[this.MarkSize + 1];

                        for(int var46 = 0; var46 < this.MarkSize; ++var46) {
                            byte[] var48 = new byte[32];
                            byte[] var25 = new byte[32];
                            byte[] var26 = new byte[32];
                            byte[] var27 = new byte[512];
                            if (this.readSocket(this.serverInput, var48, 32) != 32) {
                                var42 = -4;
                                this.closeSocket();
                                return var42;
                            }

                            if (this.readSocket(this.serverInput, var25, 32) != 32) {
                                var42 = -4;
                                this.closeSocket();
                                return var42;
                            }

                            if (this.readSocket(this.serverInput, var26, 32) != 32) {
                                var42 = -4;
                                this.closeSocket();
                                return var42;
                            }

                            if (this.readSocket(this.serverInput, var27, 512) != 512) {
                                var42 = -4;
                                this.closeSocket();
                                return var42;
                            }

                            this.TTSMark[var46] = new libttsapi.TTSMARK(this.getintValue(var48, 32), this.getintValue(var25, 32), this.getintValue(var26, 32), this.copyByteToString(var27));
                        }

                        if (var14) {
                            var41 = 1;
                        } else {
                            var41 = 0;
                        }
                    }

                    if (var41 == 1) {
                        this.closeSocket();
                    }

                    return var41;
                }
            } else {
                var42 = -10;
                return var42;
            }
        }
    }

    public int ttsRequestStatus(String var1, int var2) throws IOException {
        boolean var3 = false;
        boolean var4 = false;
        byte[] var5 = new byte[8];
        byte[] var6 = new byte[128];
        this.nVoiceLength = 0;
        this.initByte(var5, 8);
        this.initByte(var6, 128);
        int var16;
        if ((var16 = this.Check_Server(var1, var2)) != 0) {
            return var16;
        } else {
            try {
                this.socketAddress = new InetSocketAddress(var1, var2);
                this.serverSocket = new Socket();
                this.serverSocket.connect(this.socketAddress, this.nConnectTimeout);
                this.serverSocket.setSoTimeout(this.nTimeout);
                this.serverSocket.setTcpNoDelay(true);
                this.serverOutput = this.serverSocket.getOutputStream();
                this.serverInput = new BufferedInputStream(this.serverSocket.getInputStream());
            } catch (UnknownHostException var13) {
                var16 = -1;
            } catch (IOException var14) {
                var16 = -3;
            } catch (Exception var15) {
                var16 = -3;
            }

            if (var16 < 0) {
                this.closeSocket();
                return var16;
            } else {
                new String();
                this.copyStringToByte("SMON\n", var5);

                byte var17;
                try {
                    this.writeSocket(this.serverOutput, var5, 5);
                    this.serverOutput.flush();
                } catch (IOException var11) {
                    var17 = -4;
                    this.closeSocket();
                    return var17;
                } catch (Exception var12) {
                    var17 = -4;
                    this.closeSocket();
                    return var17;
                }

                this.readSocketM(this.serverInput, var6, 128);
                this.copyStringToByte("QUIT\n", var5);

                try {
                    this.writeSocket(this.serverOutput, var5, 5);
                    this.serverOutput.flush();
                } catch (IOException var9) {
                    var17 = -4;
                    this.closeSocket();
                    return var17;
                } catch (Exception var10) {
                    var17 = -4;
                    this.closeSocket();
                    return var17;
                }

                String var7 = this.copyByteToString(var6);
                if (var7.charAt(6) == '-') {
                    var16 = -1;
                } else {
                    var16 = this.getintFromChar(var7.charAt(6));
                }

                if (var16 == -1) {
                    var16 = 2;
                }

                this.closeSocket();
                return var16;
            }
        }
    }

    public int GetTimeout() {
        return this.nTimeout;
    }

    public void SetTimeout(int var1) {
        if (var1 > 0) {
            this.nTimeout = var1;
        }

    }

    public int GetConnectTimeout() {
        return this.nConnectTimeout;
    }

    public void SetConnectTimeout(int var1) {
        if (var1 > 0) {
            this.nConnectTimeout = var1;
        }

    }

    private void initByte(byte[] var1, int var2) {
        boolean var3 = false;

        for(int var4 = 0; var4 < var2; ++var4) {
            var1[var4] = 0;
        }

    }

    private void copyByte(byte[] var1, byte[] var2, int var3) {
        for(int var4 = 0; var4 < var3; ++var4) {
            var1[var4] = var2[var4];
        }

    }

    private int writeSocket(OutputStream var1, byte[] var2, int var3) {
        boolean var4 = false;
        int var5 = 0;
        boolean var6 = false;

        for(int var12 = 0; var12 < var3; ++var12) {
            byte var13 = var2[var12];
            if (var13 != 0) {
                try {
                    var1.write(var13);
                    ++var5;
                } catch (IOException var10) {
                    var5 = 0;
                    break;
                } catch (Exception var11) {
                    var5 = 0;
                    break;
                }
            } else {
                byte var14 = 0;

                try {
                    var1.write(var14);
                } catch (IOException var8) {
                    var5 = 0;
                    break;
                } catch (Exception var9) {
                    var5 = 0;
                    break;
                }
            }
        }

        return var5;
    }

    private int readSocket(BufferedInputStream var1, byte[] var2, int var3) {
        boolean var4 = false;
        int var5 = 0;
        byte[] var6 = new byte[1];

        for(int var10 = 0; var10 < var3; ++var10) {
            try {
                var1.read(var6, 0, 1);
                if (var6[0] != 0) {
                    var2[var5] = var6[0];
                } else {
                    var2[var5] = 0;
                }

                ++var5;
            } catch (IOException var8) {
                var5 = 0;
                break;
            } catch (Exception var9) {
                var5 = 0;
                break;
            }
        }

        return var5;
    }

    private int readSocketM(BufferedInputStream var1, byte[] var2, int var3) {
        boolean var4 = false;
        int var5 = 0;
        byte[] var6 = new byte[1];

        for(int var10 = 0; var10 < var3; ++var10) {
            try {
                var1.read(var6, 0, 1);
                if (var6[0] != 0) {
                    var2[var5] = var6[0];
                } else {
                    var2[var5] = 0;
                }

                ++var5;
                if (var6[0] == 10) {
                    break;
                }
            } catch (IOException var8) {
                var5 = 0;
                break;
            } catch (Exception var9) {
                var5 = 0;
                break;
            }
        }

        return var5;
    }

    private int closeSocket() {
        try {
            if (this.serverOutput != null) {
                this.serverOutput.flush();
                this.serverOutput.write(11);
            }

            if (this.serverSocket != null) {
                this.serverSocket.shutdownOutput();
                this.serverSocket.shutdownInput();
            }

            if (this.serverInput != null) {
                this.serverInput.close();
            }

            if (this.serverOutput != null) {
                this.serverOutput.close();
            }

            if (this.serverSocket != null) {
                this.serverSocket.close();
            }

            return 1;
        } catch (UnknownHostException var2) {
            return -1;
        } catch (IOException var3) {
            return -1;
        } catch (Exception var4) {
            return -1;
        }
    }

    private int copyStringToByte(String var1, byte[] var2) {
        boolean var3 = false;
        int var4 = 0;
        byte[] var5 = var1.getBytes();
        int var6 = this.getByteLen(var5);

        for(int var7 = 0; var7 < var6 && var5[var7] != 0; ++var7) {
            var2[var7] = var5[var7];
            ++var4;
        }

        return var4;
    }

    private String copyByteToString(byte[] var1) {
        boolean var2 = false;
        int var3 = this.getByteLen(var1);
        StringBuffer var4 = new StringBuffer(var3);

        for(int var5 = 0; var5 < var3; ++var5) {
            var4.append((char)var1[var5]);
        }

        return var4.toString();
    }

    private int getByteLen(byte[] var1) {
        int var2 = 0;
        boolean var3 = false;
        boolean var4 = false;
        boolean var5 = true;

        for(int var6 = 0; var5; ++var6) {
            if (var1.length <= var6) {
                var5 = false;
                break;
            }

            if (var1 == null) {
                break;
            }

            if (var1[var6] == 0) {
                var5 = false;
                break;
            }

            ++var2;
        }

        return var2;
    }

    private int getintValue(byte[] var1, int var2) {
        byte var3 = 0;
        boolean var4 = false;
        byte[] var5 = new byte[var2];
        if (var2 == 0) {
            return var3;
        } else {
            int var8;
            for(var8 = 0; var8 < var2 && var1[var8] >= 48 && var1[var8] <= 57; ++var8) {
                var5[var8] = var1[var8];
            }

            if (var8 == 0) {
                return 0;
            } else {
                String var6 = new String(var5);
                int var7;
                if (var6.trim().length() > 0) {
                    var7 = Integer.parseInt(var6.trim());
                } else {
                    var7 = 0;
                }

                return var7;
            }
        }
    }

    private int getintFromByte(byte var1) {
        boolean var2 = false;
        String var3 = "" + var1 + "";
        int var4 = Integer.parseInt(var3);
        return var4;
    }

    private int getintFromChar(char var1) {
        boolean var2 = false;
        int var3 = var1 - 48;
        return var3;
    }

    private int Check_Voiceformat(int var1, int var2) {
        if (var2 == 0) {
            if (var1 != 0 && var1 != 2 && var1 != 3 && var1 != 4 && var1 != 5) {
                return -7;
            }
        } else if (var2 == 1) {
            if (var1 != 0 && var1 != 1 && var1 != 2 && var1 != 3 && var1 != 4 && var1 != 5 && var1 != 6 && var1 != 10 && var1 != 11 && var1 != 12 && var1 != 13 && var1 != 17) {
                return -7;
            }
        } else if (var2 == 2) {
            if (var1 != 0 && var1 != 1 && var1 != 2 && var1 != 3 && var1 != 4 && var1 != 5 && var1 != 6 && var1 != 10 && var1 != 11 && var1 != 12 && var1 != 13 && var1 != 17 && var1 != 18) {
                return -7;
            }
        } else if (var2 == 3) {
            if (var1 != 0 && var1 != 2 && var1 != 3 && var1 != 4) {
                return -7;
            }
        } else if (var2 == 3) {
            if (var1 != 0 && var1 != 1 && var1 != 2 && var1 != 3 && var1 != 4 && var1 != 11 && var1 != 12 && var1 != 13 && var1 != 17 && var1 != 18) {
                return -7;
            }
        } else if (var2 == 4) {
            if (var1 != 0 && var1 != 1 && var1 != 2 && var1 != 3 && var1 != 4 && var1 != 10 && var1 != 11 && var1 != 12 && var1 != 13 && var1 != 17 && var1 != 18) {
                return -7;
            }
        } else {
            if (var2 != 5) {
                return -7;
            }

            if (var1 != 0 && var1 != 1 && var1 != 2 && var1 != 3 && var1 != 4 && var1 != 10 && var1 != 11 && var1 != 12 && var1 != 13 && var1 != 17) {
                return -7;
            }
        }

        return 0;
    }

    private int Check_Text(byte[] var1) {
        return var1 != null && this.getByteLen(var1) > 0 ? 0 : -6;
    }

    private int Check_Server(String var1, int var2) {
        if (var1 != null && var1.length() != 0) {
            return var2 > 0 && var2 < 65535 ? 0 : -1;
        } else {
            return -1;
        }
    }

    private int Check_Speed(int var1) {
        if (var1 == -1) {
            return 510;
        } else if (var1 < 0) {
            return 510;
        } else if (var1 >= 0 && var1 < 50) {
            return 50;
        } else {
            return var1 > 400 ? 400 : var1;
        }
    }

    private int Check_Volume(int var1) {
        if (var1 == -1) {
            return 510;
        } else if (var1 < 0) {
            return 510;
        } else {
            return var1 > 500 ? 500 : var1;
        }
    }

    private int Check_Pitch(int var1) {
        if (var1 == -1) {
            return 510;
        } else if (var1 < 0) {
            return 510;
        } else if (var1 >= 0 && var1 < 50) {
            return 50;
        } else {
            return var1 > 200 ? 200 : var1;
        }
    }

    private int Check_Dicnum(int var1, int var2) {
        if (var1 == -1) {
            return var2 / 100;
        } else {
            return var1 >= 0 && var1 < 1024 ? var1 : var2 / 100;
        }
    }

    private int Check_Textformat(int var1) {
        return var1 >= 0 && var1 <= 6 ? var1 : -1;
    }

    private int Check_dir_file_name(String var1, String var2, int var3) {
        int var4 = 0;
        boolean var5 = false;
        if (var1 != null) {
            var4 = var1.length();
        }

        if (var2 == null) {
            return -8;
        } else {
            int var7 = var2.length();
            if (var4 < 128 && var7 < 128 && var7 != 0) {
                if (var4 != 0 && (var1.charAt(0) == ' ' || var1.charAt(var4 - 1) == ' ')) {
                    return -8;
                } else {
                    int var6;
                    if (var3 == 17) {
                        for(var6 = 0; var6 < var4; ++var6) {
                            if (var1.charAt(var6) == ':' || var1.charAt(var6) == '*' || var1.charAt(var6) == '?' || var1.charAt(var6) == '"' || var1.charAt(var6) == '<' || var1.charAt(var6) == '>' || var1.charAt(var6) == '|' || var1.charAt(var6) == '\r' || var1.charAt(var6) == '\t' || var1.charAt(var6) == '\n' || var1.charAt(var6) == '(' || var1.charAt(var6) == ')') {
                                return -8;
                            }
                        }

                        for(var6 = 0; var6 < var7; ++var6) {
                            if (var2.charAt(var6) == '\\' || var2.charAt(var6) == '/' || var2.charAt(var6) == ':' || var2.charAt(var6) == '*' || var2.charAt(var6) == '?' || var2.charAt(var6) == '"' || var2.charAt(var6) == '<' || var2.charAt(var6) == '>' || var2.charAt(var6) == '|' || var2.charAt(var6) == '\r' || var2.charAt(var6) == '\t' || var2.charAt(var6) == '\n' || var2.charAt(var6) == '(' || var2.charAt(var6) == ')') {
                                return -8;
                            }
                        }

                        return 0;
                    } else {
                        for(var6 = 0; var6 < var4; ++var6) {
                            if (var1.charAt(var6) == ':' || var1.charAt(var6) == '*' || var1.charAt(var6) == '?' || var1.charAt(var6) == '"' || var1.charAt(var6) == '<' || var1.charAt(var6) == '>' || var1.charAt(var6) == '|' || var1.charAt(var6) == '\r' || var1.charAt(var6) == '\t' || var1.charAt(var6) == '\n') {
                                return -8;
                            }
                        }

                        for(var6 = 0; var6 < var7; ++var6) {
                            if (var2.charAt(var6) == '\\' || var2.charAt(var6) == '/' || var2.charAt(var6) == ':' || var2.charAt(var6) == '*' || var2.charAt(var6) == '?' || var2.charAt(var6) == '"' || var2.charAt(var6) == '<' || var2.charAt(var6) == '>' || var2.charAt(var6) == '|' || var2.charAt(var6) == '\r' || var2.charAt(var6) == '\t' || var2.charAt(var6) == '\n') {
                                return -8;
                            }
                        }

                        return 0;
                    }
                }
            } else {
                return -8;
            }
        }
    }

    public final class TTSMARK {
        public int nOffsetInStream;
        public int nOffsetInBuffer;
        public int nPosInText;
        public String sMarkName;

        public TTSMARK() {
            this.nOffsetInStream = 0;
            this.nOffsetInBuffer = 0;
            this.nPosInText = 0;
            this.sMarkName = "";
        }

        public TTSMARK(int var2, int var3, int var4, String var5) {
            this.nOffsetInStream = var2;
            this.nOffsetInBuffer = var3;
            this.nPosInText = var4;
            this.sMarkName = var5;
        }
    }
}
